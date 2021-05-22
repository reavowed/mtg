package mtg.turns

import mtg.SpecWithGameStateManager
import mtg.data.cards.{Forest, Plains}
import mtg.data.sets.Strixhaven
import mtg.game.actions.{ActivateAbilityAction, PlayLandAction}
import mtg.game.objects.CardObject
import mtg.game.state.PermanentStatus
import mtg.game.turns.TurnPhase.PrecombatMainPhase
import mtg.game.turns.StartNextTurnAction
import mtg.game.turns.priority.PriorityChoice
import org.specs2.matcher.Matcher

class UntapStepSpec extends SpecWithGameStateManager {
  def beTapped: Matcher[PermanentStatus] = { (permanentStatus: PermanentStatus) =>
    (permanentStatus.isTapped, "was tapped", "was not tapped")
  }

  "untap step" should {
    "untap only tapped permanents the active player controls" in {
      val plains = Strixhaven.cardPrintingsByDefinition(Plains)
      val forest = Strixhaven.cardPrintingsByDefinition(Forest)
      val initialState = gameObjectStateWithInitialLibrariesAndHands.setHand(playerOne, Seq(plains)).setHand(playerTwo, Seq(forest))

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilPhase(PrecombatMainPhase)

      val playPlainsAction = manager.currentGameState.pendingActions.head.asInstanceOf[PriorityChoice].availableActions.ofType[PlayLandAction]
        .filter(_.land.gameObject.asInstanceOf[CardObject].card.printing.cardDefinition == Plains).head
      manager.handleDecision(playPlainsAction.optionText, playerOne)
      val tapPlainsAction = manager.currentGameState.pendingActions.head.asInstanceOf[PriorityChoice].availableActions.ofType[ActivateAbilityAction].head
      manager.handleDecision(tapPlainsAction.optionText, playerOne)

      manager.passUntilTurnAndPhase(2, PrecombatMainPhase)
      val playForestAction = manager.currentGameState.pendingActions.head.asInstanceOf[PriorityChoice].availableActions.ofType[PlayLandAction]
        .filter(_.land.gameObject.asInstanceOf[CardObject].card.printing.cardDefinition == Forest).head
      manager.handleDecision(playForestAction.optionText, playerTwo)
      val tapForestAction = manager.currentGameState.pendingActions.head.asInstanceOf[PriorityChoice].availableActions.ofType[ActivateAbilityAction].head
      manager.handleDecision(tapForestAction.optionText, playerTwo)

      manager.passUntilTurnAndPhase(3, PrecombatMainPhase)

      manager.currentGameState.gameObjectState.battlefield.getCard(Plains).permanentStatus must beSome(not(beTapped))
      manager.currentGameState.gameObjectState.battlefield.getCard(Forest).permanentStatus must beSome(beTapped)
    }
  }

}
