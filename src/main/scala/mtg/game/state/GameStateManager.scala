package mtg.game.state

import mtg.abilities.TriggeredAbility
import mtg.effects.condition.EventCondition
import mtg.effects.continuous.PreventionEffect
import mtg.effects.continuous.PreventionEffect.Result.Prevent
import mtg.game.objects.FloatingActiveContinuousEffect
import mtg.game.turns.TurnPhase.{PostcombatMainPhase, PrecombatMainPhase}
import mtg.game.turns.priority.PriorityChoice
import mtg.game.{GameStartingData, PlayerId}

import scala.annotation.tailrec
import scala.collection.mutable

class GameStateManager(private var _currentGameState: GameState, val onStateUpdate: GameState => Unit, val stops: mutable.Map[PlayerId, Map[PlayerId, Seq[AnyRef]]]) {
  def currentGameState: GameState = this.synchronized { _currentGameState }

  executeAutomaticActions()

  private def executeAutomaticActions(): Unit = {
    executeAutomaticActions(currentGameState)
  }

  @tailrec
  private def executeAutomaticActions(gameState: GameState): Unit = {
    gameState.popAction() match {
      case (gameEvent: AutomaticGameAction, gameState) =>
        executeAutomaticActions(executeAutomaticAction(gameEvent, gameState))
      case (BackupAction(gameStateToRevertTo), _) =>
        executeAutomaticActions(gameStateToRevertTo)
      case (priorityChoice: PriorityChoice, gameState)
        if !stops(priorityChoice.playerToAct)(gameState.activePlayer).exists(gameState.currentStep.orElse(gameState.currentPhase).contains)
      =>
        executeDecision(priorityChoice, "Pass", gameState) match {
          case Some(gameState) =>
            executeAutomaticActions(gameState)
          case None =>
        }
      case _ =>
        _currentGameState = gameState
        onStateUpdate(_currentGameState)
    }
  }

  private def executeAutomaticAction(action: AutomaticGameAction, initialGameState: GameState): GameState = {
    val preventResult = initialGameState.gameObjectState.activeContinuousEffects.ofType[PreventionEffect]
      .findOption(_.checkAction(action, initialGameState).asOptionalInstanceOf[Prevent])

    preventResult match {
      case Some(Prevent(logEvent)) =>
        logEvent.map(initialGameState.recordLogEvent).getOrElse(initialGameState)
      case None =>
        val actionResult = action.execute(initialGameState)
        val gameObjectStateAfterAction = actionResult.newGameObjectState.getOrElse(initialGameState.gameObjectState)
        val gameStateAfterAction = initialGameState.updateGameObjectState(gameObjectStateAfterAction)
        val triggeredAbilities = getTriggeringAbilities(action, gameStateAfterAction)
        val endedEffects = getEndedEffects(action, gameStateAfterAction)

        val finalGameObjectState = if (actionResult.newGameObjectState.isDefined || triggeredAbilities.nonEmpty || endedEffects.nonEmpty) {
          gameObjectStateAfterAction
            .addWaitingTriggeredAbilities(triggeredAbilities)
            .updateEffects(_.filter(!endedEffects.contains(_)))
        } else {
          initialGameState.gameObjectState
        }

        initialGameState
          .recordAction(action)
          .updateGameObjectState(finalGameObjectState)
          .addActions(actionResult.childActions)
          .recordLogEvent(actionResult.logEvent)
    }
  }

  private def getTriggeringAbilities(action: AutomaticGameAction, gameStateAfterAction: GameState): Seq[TriggeredAbility] = {
    gameStateAfterAction.gameObjectState.activeTriggeredAbilities.filter {
      _.getCondition(gameStateAfterAction) match {
        case eventCondition: EventCondition =>
          eventCondition.matchesEvent(action, gameStateAfterAction)
      }
    }.toSeq
  }

  private def getEndedEffects(action: AutomaticGameAction, gameStateAfterAction: GameState): Seq[FloatingActiveContinuousEffect] = {
    gameStateAfterAction.gameObjectState.floatingActiveContinuousEffects.filter(effect => {
      def matchesCondition = effect.endCondition match {
        case eventCondition: EventCondition =>
          eventCondition.matchesEvent(action, gameStateAfterAction)
      }
      def objectExists = effect.effect.affectedObjects.exists(id => gameStateAfterAction.gameObjectState.allObjects.exists(_.objectId == id))
      matchesCondition || !objectExists
    })
  }

  def handleDecision(serializedDecision: String, actingPlayer: PlayerId): Unit = this.synchronized {
    currentGameState.popAction() match {
      case (choice: PlayerChoice, gameState) if choice.playerToAct == actingPlayer =>
        executeDecision(choice, serializedDecision, gameState) match {
          case Some(gameState) =>
            executeAutomaticActions(gameState)
          case None =>
        }
      case _ =>
    }
  }

  def executeDecision(choice: PlayerChoice, serializedDecision: String, gameState: GameState): Option[GameState] = {
    choice.handleDecision(serializedDecision, gameState) match {
      case Some((decision, actionResult)) =>
        Some(gameState.recordGameEvent(decision).handleActionResult(actionResult))
      case None =>
        None
    }
  }
}

object GameStateManager {
  def initial(gameStartingData: GameStartingData, onStateUpdate: GameState => Unit): GameStateManager = {
    val initialStops = mutable.Map(gameStartingData.players.map(p =>
      p -> gameStartingData.players.map[(PlayerId, Seq[AnyRef])](q => q -> (if (p == q) Seq(PrecombatMainPhase, PostcombatMainPhase) else Nil)).toMap): _*)
    new GameStateManager(GameState.initial(gameStartingData), onStateUpdate, initialStops)
  }
}
