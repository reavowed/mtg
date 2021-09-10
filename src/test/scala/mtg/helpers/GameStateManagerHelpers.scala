package mtg.helpers

import mtg.cards.CardDefinition
import mtg.game.actions.{ActivateAbilityAction, PlayLandSpecialAction}
import mtg.game.{ObjectId, PlayerId, Zone}
import mtg.game.objects.{GameObject, GameObjectState, PermanentObject}
import mtg.game.state.{GameAction, GameState, GameStateManager, ObjectWithState}
import mtg.game.turns.{TurnPhase, TurnStep}
import mtg.game.turns.priority.PriorityChoice
import mtg._
import mtg.game.actions.cast.CastSpellAction
import mtg.game.turns.turnBasedActions.DeclareAttackersChoice

trait GameStateManagerHelpers extends GameObjectHelpers with GameObjectStateHelpers {

  implicit class GameStateManagerOps(gameStateManager: GameStateManager) {
    def updateGameState(f: GameState => GameState): GameStateManager = {
      new GameStateManager(f(gameStateManager.currentGameState), gameStateManager.onStateUpdate, gameStateManager.stops)
    }
    def updateGameObjectState(f: GameObjectState => GameObjectState): GameStateManager = {
      updateGameState(_.updateGameObjectState(f(gameStateManager.currentGameState.gameObjectState)))
    }

    def getPermanent(cardDefinition: CardDefinition): PermanentObject = {
      gameStateManager.currentGameState.gameObjectState.getPermanent(cardDefinition)
    }
    def getCard(cardDefinition: CardDefinition): GameObject = {
      gameStateManager.currentGameState.gameObjectState.getCard(cardDefinition)
    }
    def getCards(cardDefinitions: CardDefinition*): Seq[GameObject] = {
      gameStateManager.currentGameState.gameObjectState.getCards(cardDefinitions: _*)
    }
    def getCard(zone: Zone, cardDefinition: CardDefinition): GameObject = {
      gameStateManager.currentGameState.gameObjectState.getCard(zone, cardDefinition)
    }
    def getState(gameObject: GameObject): ObjectWithState = {
      gameObject.currentState(gameStateManager.currentGameState)
    }
    def getState(zone: Zone, cardDefinition: CardDefinition): ObjectWithState = {
      getState(getCard(zone, cardDefinition))
    }

    def currentAction: GameAction = gameStateManager.currentGameState.pendingActions.head

    def passPriority(player: PlayerId): Unit = {
      gameStateManager.handleDecision("Pass", player)
    }
    private def passUntil(predicate: GameState => Boolean): Unit = {
      while (!predicate(gameStateManager.currentGameState)) {
        currentAction match {
          case choice: PriorityChoice =>
            passPriority(choice.playerToAct)
          case choice: DeclareAttackersChoice =>
            gameStateManager.handleDecision("", choice.playerToAct)
          case _ =>
            return
        }
      }
    }
    def passUntilTurn(turnNumber: Int): Unit = {
      passUntil(_.turnState.currentTurnNumber == turnNumber)
    }
    def passUntilPhase(turnPhase: TurnPhase): Unit = {
      passUntil(_.turnState.currentPhase.contains(turnPhase))
    }
    def passUntilStep(turnStep: TurnStep): Unit = {
      passUntil(_.turnState.currentStep.contains(turnStep))
    }
    def passUntilTurnAndPhase(turnNumber: Int, turnPhase: TurnPhase): Unit = {
      passUntilTurn(turnNumber)
      passUntilPhase(turnPhase)
    }
    def passUntilTurnAndStep(turnNumber: Int, turnStep: TurnStep): Unit = {
      passUntilTurn(turnNumber)
      passUntilStep(turnStep)
    }
    def passUntilStackEmpty(): Unit = {
      passUntil(_.gameObjectState.stack.isEmpty)
    }
    def resolveNext(): Unit = {
      val stackSize = gameStateManager.currentGameState.gameObjectState.stack.size
      passUntil(_.gameObjectState.stack.size < stackSize)
    }

    def playLand(player: PlayerId, cardDefinition: CardDefinition): Unit = {
      val landAction = currentAction.asInstanceOf[PriorityChoice].availableActions.ofType[PlayLandSpecialAction]
        .filter(_.land.gameObject.isCard(cardDefinition)).single
      gameStateManager.handleDecision(landAction.optionText, player)
    }
    def playFirstLand(player: PlayerId, cardDefinition: CardDefinition): Unit = {
      val landAction = currentAction.asInstanceOf[PriorityChoice].availableActions.ofType[PlayLandSpecialAction]
        .filter(_.land.gameObject.isCard(cardDefinition)).head
      gameStateManager.handleDecision(landAction.optionText, player)
    }
    def activateAbility(player: PlayerId, cardDefinition: CardDefinition): Unit = {
      val abilityAction = currentAction.asInstanceOf[PriorityChoice].availableActions.ofType[ActivateAbilityAction]
        .filter(_.objectWithAbility.gameObject.isCard(cardDefinition)).single
      gameStateManager.handleDecision(abilityAction.optionText, player)
    }
    def activateFirstAbility(player: PlayerId, cardDefinition: CardDefinition): Unit = {
      val abilityAction = currentAction.asInstanceOf[PriorityChoice].availableActions.ofType[ActivateAbilityAction]
        .filter(_.objectWithAbility.gameObject.isCard(cardDefinition)).head
      gameStateManager.handleDecision(abilityAction.optionText, player)
    }
    def activateAbilities(player: PlayerId, cardDefinition: CardDefinition, number: Int): Unit = {
      (1 to number).foreach(_ => activateFirstAbility(player, cardDefinition))
    }


    def castSpell(player: PlayerId, cardDefinition: CardDefinition): Unit = {
      val abilityAction = currentAction.asInstanceOf[PriorityChoice].availableActions.ofType[CastSpellAction]
        .filter(_.objectToCast.gameObject.isCard(cardDefinition)).single
      gameStateManager.handleDecision(abilityAction.optionText, player)
    }
    def castFirstSpell(player: PlayerId, cardDefinition: CardDefinition): Unit = {
      val abilityAction = currentAction.asInstanceOf[PriorityChoice].availableActions.ofType[CastSpellAction]
        .filter(_.objectToCast.gameObject.isCard(cardDefinition)).head
      gameStateManager.handleDecision(abilityAction.optionText, player)
    }
    def attackWith(player: PlayerId, cardDefinitions: CardDefinition*): Unit = {
      gameStateManager.handleDecision(cardDefinitions.map(d => getCard(Zone.Battlefield, d).objectId.sequentialId.toString).mkString(" "), player)
    }
    def block(player: PlayerId, blocker: CardDefinition, attacker: CardDefinition): Unit = {
      block(player, (blocker, attacker))
    }
    def block(player: PlayerId, blockers: (CardDefinition, CardDefinition)*): Unit = {
      val serializedDecision = blockers.map { case (blocker, attacker) =>
        getCard(Zone.Battlefield, blocker).objectId.sequentialId.toString + " " +
          getCard(Zone.Battlefield, attacker).objectId.sequentialId.toString + " "
      }.mkString(" ")

      gameStateManager.handleDecision(serializedDecision, player)
    }
    def orderBlocks(player: PlayerId, blockers: CardDefinition*): Unit = {
      val serializedDecision = blockers.map { blocker =>
        getCard(Zone.Battlefield, blocker).objectId.sequentialId.toString
      }.mkString(" ")
      gameStateManager.handleDecision(serializedDecision, player)
    }
    def assignDamage(player: PlayerId, damage: (CardDefinition, Int)*): Unit = {
      val serializedDecision = damage.map { case (blocker, amount) =>
        getCard(Zone.Battlefield, blocker).objectId.sequentialId.toString + " " + amount
      }.mkString(" ")

      gameStateManager.handleDecision(serializedDecision, player)
    }

    def chooseCard(player: PlayerId, cardDefinition: CardDefinition): Unit = {
      gameStateManager.handleDecision(getCard(cardDefinition).objectId.toString, player)
    }
    def choosePlayer(player: PlayerId, chosenPlayer: PlayerId): Unit = {
      gameStateManager.handleDecision(chosenPlayer.id, player)
    }
  }

  def getId(cardDefinition: CardDefinition)(implicit gameStateManager: GameStateManager): ObjectId = {
    gameStateManager.getCard(cardDefinition).objectId
  }

  implicit def gameObjectStateFromManager(implicit gameStateManager: GameStateManager): GameObjectState = gameStateManager.currentGameState.gameObjectState
}
