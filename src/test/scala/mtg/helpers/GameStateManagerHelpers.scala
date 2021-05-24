package mtg.helpers

import mtg.abilities.ActivatedAbilityDefinition
import mtg.cards.CardDefinition
import mtg.game.actions.{ActivateAbilityAction, PlayLandAction}
import mtg.game.actions.cast.CastSpellAction
import mtg.game.{PlayerIdentifier, Zone}
import mtg.game.objects.{CardObject, GameObject, GameObjectState}
import mtg.game.state.{GameAction, GameState, GameStateManager, ObjectWithState}
import mtg.game.turns.TurnPhase
import mtg.game.turns.priority.PriorityChoice

trait GameStateManagerHelpers extends GameObjectHelpers {

  implicit class GameStateManagerOps(gameStateManager: GameStateManager) {
    def updateGameState(f: GameState => GameState): GameStateManager = {
      new GameStateManager(f(gameStateManager.currentGameState), gameStateManager.onStateUpdate, gameStateManager.stops)
    }
    def updateGameObjectState(f: GameObjectState => GameObjectState): GameStateManager = {
      updateGameState(_.updateGameObjectState(f(gameStateManager.currentGameState.gameObjectState)))
    }

    def getCard(zone: Zone, cardDefinition: CardDefinition): CardObject = {
      zone.getState(gameStateManager.currentGameState.gameObjectState).getCard(cardDefinition)
    }
    def getState(gameObject: GameObject): ObjectWithState = {
      gameStateManager.currentGameState.derivedState.objectStates(gameObject.objectId)
    }
    def getState(zone: Zone, cardDefinition: CardDefinition): ObjectWithState = {
      getState(getCard(zone, cardDefinition))
    }

    def currentAction: GameAction = gameStateManager.currentGameState.pendingActions.head

    def passPriority(player: PlayerIdentifier): Unit = {
      gameStateManager.handleDecision("Pass", player)
    }
    private def passUntil(predicate: GameState => Boolean): Unit = {
      while (!predicate(gameStateManager.currentGameState) && gameStateManager.currentGameState.pendingActions.head.isInstanceOf[PriorityChoice]) {
        passPriority(gameStateManager.currentGameState.pendingActions.head.asInstanceOf[PriorityChoice].playerToAct)
      }
    }
    def passUntilTurn(turnNumber: Int): Unit = {
      passUntil(_.currentTurnNumber == turnNumber)
    }
    def passUntilPhase(turnPhase: TurnPhase): Unit = {
      passUntil(_.currentPhase.contains(turnPhase))
    }
    def passUntilTurnAndPhase(turnNumber: Int, turnPhase: TurnPhase): Unit = {
      passUntilTurn(turnNumber)
      passUntilPhase(turnPhase)
    }

    def playLand(player: PlayerIdentifier, cardDefinition: CardDefinition): Unit = {
      val landAction = currentAction.asInstanceOf[PriorityChoice].availableActions.ofType[PlayLandAction]
        .filter(_.land.gameObject.asOptionalInstanceOf[CardObject].exists(_.card.printing.cardDefinition == cardDefinition)).single
      gameStateManager.handleDecision(landAction.optionText, player)
    }
    def playFirstLand(player: PlayerIdentifier, cardDefinition: CardDefinition): Unit = {
      val landAction = currentAction.asInstanceOf[PriorityChoice].availableActions.ofType[PlayLandAction]
        .filter(_.land.gameObject.asOptionalInstanceOf[CardObject].exists(_.card.printing.cardDefinition == cardDefinition)).head
      gameStateManager.handleDecision(landAction.optionText, player)
    }
    def activateAbility(player: PlayerIdentifier, cardDefinition: CardDefinition): Unit = {
      val abilityAction = currentAction.asInstanceOf[PriorityChoice].availableActions.ofType[ActivateAbilityAction]
        .filter(_.source.gameObject.asOptionalInstanceOf[CardObject].exists(_.card.printing.cardDefinition == cardDefinition)).single
      gameStateManager.handleDecision(abilityAction.optionText, player)
    }
    def activateFirstAbility(player: PlayerIdentifier, cardDefinition: CardDefinition): Unit = {
      val abilityAction = currentAction.asInstanceOf[PriorityChoice].availableActions.ofType[ActivateAbilityAction]
        .filter(_.source.gameObject.asOptionalInstanceOf[CardObject].exists(_.card.printing.cardDefinition == cardDefinition)).head
      gameStateManager.handleDecision(abilityAction.optionText, player)
    }
    def castSpell(player: PlayerIdentifier, cardDefinition: CardDefinition): Unit = {
      val abilityAction = currentAction.asInstanceOf[PriorityChoice].availableActions.ofType[CastSpellAction]
        .filter(_.objectToCast.gameObject.asOptionalInstanceOf[CardObject].exists(_.card.printing.cardDefinition == cardDefinition)).single
      gameStateManager.handleDecision(abilityAction.optionText, player)
    }
    def castFirstSpell(player: PlayerIdentifier, cardDefinition: CardDefinition): Unit = {
      val abilityAction = currentAction.asInstanceOf[PriorityChoice].availableActions.ofType[CastSpellAction]
        .filter(_.objectToCast.gameObject.asOptionalInstanceOf[CardObject].exists(_.card.printing.cardDefinition == cardDefinition)).head
      gameStateManager.handleDecision(abilityAction.optionText, player)
    }
  }
}
