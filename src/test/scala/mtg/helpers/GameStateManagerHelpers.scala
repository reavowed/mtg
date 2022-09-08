package mtg.helpers

import mtg._
import mtg.cards.CardDefinition
import mtg.definitions.zones.Zone
import mtg.definitions.{ObjectId, PlayerId}
import mtg.game.objects.{GameObject, GameObjectState, PermanentObject}
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
    def getPermanent(cardDefinition: CardDefinition, controller: PlayerId): PermanentObject = {
      gameStateManager.gameState.gameObjectState.getPermanent(cardDefinition, controller)
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
    def getCard(cardDefinition: CardDefinition, owner: PlayerId): GameObject = {
      gameStateManager.gameState.gameObjectState.getCard(cardDefinition, owner)
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
    def attackWith(cardDefinitions: CardDefinition*): Unit = {
      val player = gameStateManager.gameState.activePlayer
      gameStateManager.handleDecision(cardDefinitions.map(d => getPermanent(d, player).objectId.toString).mkString(" "), player)
    }
    def block(blocker: CardDefinition, attacker: CardDefinition): Unit = {
      block((blocker, attacker))
    }
    def block(blockers: (CardDefinition, CardDefinition)*): Unit = {
      val attackingPlayer = gameStateManager.gameState.activePlayer
      val blockingPlayer = gameStateManager.gameState.playersInApnapOrder(1)
      val serializedDecision = blockers.map { case (blocker, attacker) =>
        getPermanent(blocker, blockingPlayer).objectId.toString + " " +
          getCard(attacker, attackingPlayer).objectId.toString + " "
      }.mkString(" ")

      gameStateManager.handleDecision(serializedDecision, blockingPlayer)
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
  def getPermanentId(cardDefinition: CardDefinition, controller: PlayerId)(implicit gameStateManager: GameStateManager): ObjectId = {
    gameStateManager.getPermanent(cardDefinition, controller).objectId
  }

  implicit def gameStateFromManager(implicit gameStateManager: GameStateManager): GameState = gameStateManager.gameState
  implicit def gameObjectStateFromManager(implicit gameStateManager: GameStateManager): GameObjectState = gameStateManager.gameState.gameObjectState
}
