package mtg.helpers

import mtg._
import mtg.cards.CardDefinition
import mtg.core.zones.Zone
import mtg.core.{ObjectId, PlayerId}
import mtg.game.objects.{BasicGameObject, GameObject, GameObjectState, PermanentObject, StackObject}
import mtg.game.priority.PriorityChoice
import mtg.game.priority.actions.{ActivateAbilityAction, CastSpellAction, PlayLandAction, PriorityAction}
import mtg.game.state._
import mtg.game.turns.turnBasedActions.DeclareAttackersChoice
import mtg.game.turns.{TurnPhase, TurnStep}
import mtg.stack.adding.PayManaChoice

trait GameStateManagerHelpers extends GameObjectHelpers with GameObjectStateHelpers {

  implicit class GameStateOps(gameState: GameState) {
    def currentChoice: Option[Choice[_]] = gameState.allCurrentActions.lastOption.flatMap(_.asOptionalInstanceOf[Choice[_]])
  }

  implicit class GameStateManagerOps(gameStateManager: GameStateManager) {
    def updateGameState(f: GameState => GameState): GameStateManager = {
      new GameStateManager(f(gameStateManager.gameState), gameStateManager.onStateUpdate, gameStateManager.stops)
    }
    def updateGameObjectState(f: GameObjectState => GameObjectState): GameStateManager = {
      updateGameState(_.updateGameObjectState(f(gameStateManager.gameState.gameObjectState)))
    }

    def getPermanent(cardDefinition: CardDefinition): PermanentObject = {
      gameStateManager.gameState.gameObjectState.getPermanent(cardDefinition)
    }
    def getPermanent(cardDefinition: CardDefinition, owner: PlayerId): PermanentObject = {
      gameStateManager.gameState.gameObjectState.getPermanent(cardDefinition, owner)
    }
    def getCard(cardDefinition: CardDefinition): GameObject = {
      gameStateManager.gameState.gameObjectState.getCard(cardDefinition)
    }
    def getCards(cardDefinitions: CardDefinition*): Seq[GameObject] = {
      gameStateManager.gameState.gameObjectState.getCards(cardDefinitions: _*)
    }
    def getCard(zone: Zone, cardDefinition: CardDefinition): GameObject = {
      gameStateManager.gameState.gameObjectState.getCard(zone, cardDefinition)
    }
    def getState(gameObject: GameObject): ObjectWithState = {
      gameStateManager.gameState.gameObjectState.derivedState.allObjectStates(gameObject.objectId)
    }
    def getState(zone: Zone, cardDefinition: CardDefinition): ObjectWithState = {
      getState(getCard(zone, cardDefinition))
    }

    def currentChoice: Option[Choice[_]] = gameStateManager.gameState.currentChoice

    def passPriority(player: PlayerId): Unit = {
      gameStateManager.handleDecision("Pass", player)
    }
    private def passUntil(predicate: GameState => Boolean): Unit = {
      while (!predicate(gameStateManager.gameState)) {
        currentChoice match {
          case Some(choice: PriorityChoice) =>
            passPriority(choice.playerToAct)
          case Some(choice: DeclareAttackersChoice) =>
            gameStateManager.handleDecision("", choice.playerToAct)
          case _ =>
            return
        }
      }
    }
    def passUntilTurn(turnNumber: Int): Unit = {
      passUntil(_.currentTurnNumber == turnNumber)
    }
    def passUntilPhase(turnPhase: TurnPhase): Unit = {
      passUntil(_.currentPhase.contains(turnPhase))
    }
    def passUntilStep(turnStep: TurnStep): Unit = {
      passUntil(_.currentStep.contains(turnStep))
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
      val objectToResolveId = gameStateManager.gameState.gameObjectState.stack.last.objectId
      passUntil(gameState => !gameState.gameObjectState.stack.exists(_.objectId == objectToResolveId))
    }

    def priorityActions: Seq[PriorityAction] = {
      val choice = currentChoice.get
      choice.asOptionalInstanceOf[PriorityChoice].map(_.availableActions)
        .orElse(choice.asOptionalInstanceOf[PayManaChoice].map(_.availableManaAbilities))
        .get
    }

    def playLand(player: PlayerId, cardDefinition: CardDefinition): Unit = {
      val landAction = priorityActions.ofType[PlayLandAction]
        .filter(_.land.gameObject.isCard(cardDefinition)).single
      gameStateManager.handleDecision(landAction.optionText, player)
    }
    def playFirstLand(player: PlayerId, cardDefinition: CardDefinition): Unit = {
      val landAction = priorityActions.ofType[PlayLandAction]
        .filter(_.land.gameObject.isCard(cardDefinition)).head
      gameStateManager.handleDecision(landAction.optionText, player)
    }
    def activateAbility(player: PlayerId, cardDefinition: CardDefinition): Unit = {
      val abilityAction = priorityActions.ofType[ActivateAbilityAction]
        .filter(_.objectWithAbility.gameObject.isCard(cardDefinition)).single
      gameStateManager.handleDecision(abilityAction.optionText, player)
    }
    def activateFirstAbility(player: PlayerId, cardDefinition: CardDefinition): Unit = {
      val abilityAction = priorityActions.ofType[ActivateAbilityAction]
        .filter(_.objectWithAbility.gameObject.isCard(cardDefinition)).head
      gameStateManager.handleDecision(abilityAction.optionText, player)
    }
    def activateAbilities(player: PlayerId, cardDefinition: CardDefinition, number: Int): Unit = {
      (1 to number).foreach(_ => activateFirstAbility(player, cardDefinition))
    }


    def castSpell(player: PlayerId, cardDefinition: CardDefinition): Unit = {
      val abilityAction = priorityActions.ofType[CastSpellAction]
        .filter(_.objectToCast.gameObject.isCard(cardDefinition)).single
      gameStateManager.handleDecision(abilityAction.optionText, player)
    }
    def castFirstSpell(player: PlayerId, cardDefinition: CardDefinition): Unit = {
      val abilityAction = priorityActions.ofType[CastSpellAction]
        .filter(_.objectToCast.gameObject.isCard(cardDefinition)).head
      gameStateManager.handleDecision(abilityAction.optionText, player)
    }
    def attackWith(player: PlayerId, cardDefinitions: CardDefinition*): Unit = {
      gameStateManager.handleDecision(cardDefinitions.map(d => getCard(Zone.Battlefield, d).objectId.toString).mkString(" "), player)
    }
    def block(player: PlayerId, blocker: CardDefinition, attacker: CardDefinition): Unit = {
      block(player, (blocker, attacker))
    }
    def block(player: PlayerId, blockers: (CardDefinition, CardDefinition)*): Unit = {
      val serializedDecision = blockers.map { case (blocker, attacker) =>
        getCard(Zone.Battlefield, blocker).objectId.toString + " " +
          getCard(Zone.Battlefield, attacker).objectId.toString + " "
      }.mkString(" ")

      gameStateManager.handleDecision(serializedDecision, player)
    }
    def orderBlocks(player: PlayerId, blockers: CardDefinition*): Unit = {
      val serializedDecision = blockers.map { blocker =>
        getCard(Zone.Battlefield, blocker).objectId.toString
      }.mkString(" ")
      gameStateManager.handleDecision(serializedDecision, player)
    }
    def assignDamage(player: PlayerId, damage: (CardDefinition, Int)*): Unit = {
      val serializedDecision = damage.map { case (blocker, amount) =>
        getCard(Zone.Battlefield, blocker).objectId.toString + " " + amount
      }.mkString(" ")

      gameStateManager.handleDecision(serializedDecision, player)
    }

    def chooseCard(player: PlayerId, cardDefinition: CardDefinition): Unit = {
      gameStateManager.handleDecision(getCard(cardDefinition).objectId.toString, player)
    }
    def choosePlayer(player: PlayerId, chosenPlayer: PlayerId): Unit = {
      gameStateManager.handleDecision(chosenPlayer.toString, player)
    }
    def chooseMode(player: PlayerId, modeIndex: Int): Unit = {
      gameStateManager.handleDecision(modeIndex.toString, player)
    }
  }

  def getId(cardDefinition: CardDefinition)(implicit gameStateManager: GameStateManager): ObjectId = {
    gameStateManager.getCard(cardDefinition).objectId
  }

  implicit def gameStateFromManager(implicit gameStateManager: GameStateManager): GameState = gameStateManager.gameState
  implicit def gameObjectStateFromManager(implicit gameStateManager: GameStateManager): GameObjectState = gameStateManager.gameState.gameObjectState
}
