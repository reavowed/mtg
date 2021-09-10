package mtg.game.state

import mtg.effects.condition.EventCondition
import mtg.effects.continuous.EventPreventionEffect
import mtg.game.turns.TurnCycleEventPreventer
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
      case (internalGameAction: InternalGameAction, gameState) =>
        executeAutomaticActions(executeInternalGameAction(internalGameAction, gameState))
      case (gameEvent: GameEvent, gameState) =>
        executeAutomaticActions(executeGameEvent(gameEvent, gameState))
      case (BackupAction(gameStateToRevertTo), _) =>
        executeAutomaticActions(gameStateToRevertTo)
      case (priorityChoice: PriorityChoice, gameState)
        if !stops(priorityChoice.playerToAct)(gameState.activePlayer).exists(gameState.turnState.currentStep.orElse(gameState.turnState.currentPhase).contains)
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

  private def executeInternalGameAction(internalGameAction: InternalGameAction, gameState: GameState): GameState = {
    gameState.handleActionResult(internalGameAction.execute(gameState))
  }

  private def executeGameEvent(gameEvent: GameEvent, initialGameState: GameState): GameState = {
    def actuallyExecuteEvent = gameEvent match {
      case gameObjectEvent: GameObjectAction =>
        executeGameObjectEvent(gameObjectEvent, _)
      case turnCycleEvent: TurnCycleAction =>
        executeTurnCycleEvent(turnCycleEvent, _)
    }
    def createTriggeredAbilities = (gameState: GameState) => {
      val triggeredAbilities = gameState.gameObjectState.activeTriggeredAbilities.filter { _.getCondition(gameState) match {
        case eventCondition: EventCondition =>
          eventCondition.matchesEvent(gameEvent, gameState)
      }}.toSeq
      gameState.updateGameObjectState(_.addWaitingTriggeredAbilities(triggeredAbilities))
    }
    def removeEndedEffects = (gameState: GameState) => gameState.updateGameObjectState(_.updateEffects(_.filter(activeEffect => {
      val objectExists = gameState.gameObjectState.allObjects.exists(_.objectId == activeEffect.effect.affectedObject)
      val matchesCondition = activeEffect.endCondition match {
        case eventCondition: EventCondition =>
          eventCondition.matchesEvent(gameEvent, gameState)
      }
      objectExists && !matchesCondition
    })))
    Seq(actuallyExecuteEvent, createTriggeredAbilities, removeEndedEffects).foldLeft(initialGameState)((gs, f) => f(gs))
  }

  private def executeGameObjectEvent(gameObjectEvent: GameObjectAction, gameState: GameState): GameState = {
    if (shouldPreventGameObjectEvent(gameObjectEvent, gameState)) {
      gameState
    } else {
      gameState.handleActionResult(gameObjectEvent, gameObjectEvent.execute(gameState))
    }
  }

  private def shouldPreventGameObjectEvent(gameObjectEvent: GameObjectAction, gameState: GameState): Boolean = {
    gameState.gameObjectState.activeContinuousEffects
      .ofType[EventPreventionEffect]
      .exists(_.preventsEvent(gameObjectEvent, gameState))
  }

  private def executeTurnCycleEvent(turnCycleEvent: TurnCycleAction, gameState: GameState): GameState = {
    val preventResult = TurnCycleEventPreventer.fromRules.collectFirst(Function.unlift(_.checkEvent(turnCycleEvent, gameState).asOptionalInstanceOf[TurnCycleEventPreventer.Result.Prevent]))
    preventResult match {
      case Some(TurnCycleEventPreventer.Result.Prevent(logEvent)) =>
        logEvent.map(gameState.recordLogEvent).getOrElse(gameState)
      case _ =>
        val (newTurnState, actionResult) = turnCycleEvent.execute(gameState)
        gameState.copy(turnState = newTurnState).handleActionResult(actionResult)
    }
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
