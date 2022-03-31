package mtg.sets.strixhaven.cards

import mtg.SpecWithGameStateManager
import mtg.abilities.AbilityDefinition
import mtg.abilities.keyword.Vigilance
import mtg.game.turns.TurnStep
import mtg.sets.alpha.cards.SavannahLions

class CombatProfessorSpec extends SpecWithGameStateManager {
  "Combat Professor" should {
    "trigger at start of combat" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, CombatProfessor, SavannahLions)
        .setBattlefield(playerTwo, AgelessGuardian)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilStep(TurnStep.BeginningOfCombatStep)

      manager.currentChoice must beSome(beTargetChoice.withAvailableTargets(CombatProfessor, SavannahLions))
    }

    "grant trigger effect" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, CombatProfessor, SavannahLions)
        .setBattlefield(playerTwo, AgelessGuardian)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilStep(TurnStep.BeginningOfCombatStep)
      manager.chooseCard(playerOne, SavannahLions)
      manager.resolveNext()

      manager.getState(manager.getCard(SavannahLions)).characteristics.power must beSome(3)
      manager.getState(manager.getCard(SavannahLions)).characteristics.toughness must beSome(1)
      manager.getState(manager.getCard(SavannahLions)).characteristics.abilities must contain[AbilityDefinition](Vigilance)
    }
  }
}
