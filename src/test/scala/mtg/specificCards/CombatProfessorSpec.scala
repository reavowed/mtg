package mtg.specificCards

import mtg.SpecWithGameStateManager
import mtg.abilities.AbilityDefinition
import mtg.abilities.keyword.Vigilance
import mtg.data.cards.alpha.SavannahLions
import mtg.data.cards.strixhaven.{AgelessGuardian, CombatProfessor}
import mtg.game.turns.{StartNextTurnAction, TurnStep}

class CombatProfessorSpec extends SpecWithGameStateManager {
  "Combat Professor" should {
    "trigger at start of combat" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, Seq(CombatProfessor, SavannahLions))
        .setBattlefield(playerTwo, Seq(AgelessGuardian))

      implicit val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilStep(TurnStep.BeginningOfCombatStep)

      manager.currentAction must beTargetChoice.withAvailableTargets(CombatProfessor, SavannahLions)
    }

    "grant trigger effect" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, Seq(CombatProfessor, SavannahLions))
        .setBattlefield(playerTwo, Seq(AgelessGuardian))

      implicit val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilStep(TurnStep.BeginningOfCombatStep)
      manager.chooseCard(playerOne, SavannahLions)
      manager.resolveNext()

      manager.getState(manager.getCard(SavannahLions)).characteristics.power must beSome(3)
      manager.getState(manager.getCard(SavannahLions)).characteristics.toughness must beSome(1)
      manager.getState(manager.getCard(SavannahLions)).characteristics.abilities must contain[AbilityDefinition](Vigilance)
    }
  }
}
