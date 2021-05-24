package mtg.helpers

import mtg.game.PlayerIdentifier
import mtg.game.actions.{ActivateAbilityAction, PlayLandAction}
import mtg.game.actions.cast.CastSpellAction
import mtg.game.state.GameAction
import mtg.game.turns.priority.PriorityChoice
import org.specs2.matcher.{Expectable, MatchResult, Matcher}
import org.specs2.mutable.SpecificationLike

import scala.collection.mutable.ListBuffer

trait GameActionHelpers extends SpecificationLike {
  class PriorityChoiceMatcher extends Matcher[GameAction] {
    private val baseMatcher: Matcher[GameAction] = beAnInstanceOf[PriorityChoice]
    private val otherMatchers: ListBuffer[Matcher[PriorityChoice]] = ListBuffer()
    private def finalMatcher: Matcher[GameAction] = otherMatchers.foldLeft(baseMatcher)((m1, m2) => m1.and(m2 ^^ {(_: GameAction).asInstanceOf[PriorityChoice]}))

    override def apply[S <: GameAction](t: Expectable[S]): MatchResult[S] = finalMatcher.apply(t)

    def forPlayer(playerIdentifier: PlayerIdentifier): PriorityChoiceMatcher = {
      otherMatchers.addOne(((_: PriorityChoice).playerToAct) ^^ beTypedEqualTo(playerIdentifier))
      this
    }
    def withAvailableAbility(abilityMatcher: Matcher[ActivateAbilityAction]): PriorityChoiceMatcher = {
      withAvailableAbilities(contain(abilityMatcher))
    }
    def withAvailableAbilities(abilityMatcher: Matcher[Seq[ActivateAbilityAction]]): PriorityChoiceMatcher = {
      otherMatchers.addOne(((_: PriorityChoice).availableActions.ofType[ActivateAbilityAction]) ^^ abilityMatcher)
      this
    }
    def withAvailableSpell(spellMatcher: Matcher[CastSpellAction]): PriorityChoiceMatcher = {
      withAvailableSpells(contain(spellMatcher))
    }
    def withAvailableSpells(spellMatcher: Matcher[Seq[CastSpellAction]]): PriorityChoiceMatcher = {
      otherMatchers.addOne(((_: PriorityChoice).availableActions.ofType[CastSpellAction]) ^^ spellMatcher)
      this
    }
    def withAvailableLands(landMatcher: Matcher[Seq[PlayLandAction]]): PriorityChoiceMatcher = {
      otherMatchers.addOne(((_: PriorityChoice).availableActions.ofType[PlayLandAction]) ^^ landMatcher)
      this
    }
  }

  def bePriorityChoice: PriorityChoiceMatcher = new PriorityChoiceMatcher
}
