package mtg.helpers

import mtg.cards.CardDefinition
import mtg.effects.oneshot.OneShotEffectChoice
import mtg.game.actions.cast.CastSpellAction
import mtg.game.actions.{ActivateAbilityAction, PlayLandSpecialAction}
import mtg.game.objects.{Card, GameObject, GameObjectState}
import mtg.game.stack.ResolveEffectChoice
import mtg.game.stack.steps.TargetChoice
import mtg.game.state.GameAction
import mtg.game.turns.priority.PriorityChoice
import mtg.game.{ObjectOrPlayer, PlayerId}
import org.specs2.matcher.{Expectable, MatchResult, Matcher}
import org.specs2.mutable.SpecificationLike

import scala.collection.mutable.ListBuffer
import scala.reflect.ClassTag

trait GameActionHelpers extends SpecificationLike with GameObjectStateHelpers {
  def beCastSpellAction(cardDefinition: CardDefinition): Matcher[CastSpellAction] = beCardObject(cardDefinition) ^^ ((_: CastSpellAction).objectToCast.gameObject)
  def beCastSpellAction(gameObject: GameObject): Matcher[CastSpellAction] = { (castSpellAction: CastSpellAction) =>
    (castSpellAction.objectToCast.gameObject == gameObject, "was given object", "was not given object")
  }

  class PriorityChoiceMatcher extends Matcher[GameAction] {
    private val baseMatcher: Matcher[GameAction] = beAnInstanceOf[PriorityChoice]
    private val otherMatchers: ListBuffer[Matcher[PriorityChoice]] = ListBuffer()
    private def finalMatcher: Matcher[GameAction] = otherMatchers.foldLeft(baseMatcher)((m1, m2) => m1.and(m2 ^^ {(_: GameAction).asInstanceOf[PriorityChoice]}))

    override def apply[S <: GameAction](t: Expectable[S]): MatchResult[S] = finalMatcher.apply(t)

    def forPlayer(playerIdentifier: PlayerId): PriorityChoiceMatcher = {
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

    def withAvailableSpell(cardDefinition: CardDefinition): PriorityChoiceMatcher = {
      otherMatchers.addOne(((_: PriorityChoice).availableActions.ofType[CastSpellAction]) ^^ contain(beCastSpellAction(cardDefinition)))
      this
    }
    def withNoAvailableSpells: PriorityChoiceMatcher = {
      otherMatchers.addOne(((_: PriorityChoice).availableActions.ofType[CastSpellAction]) ^^ beEmpty)
      this
    }

    def withAvailableLands(landMatcher: Matcher[Seq[PlayLandSpecialAction]]): PriorityChoiceMatcher = {
      otherMatchers.addOne(((_: PriorityChoice).availableActions.ofType[PlayLandSpecialAction]) ^^ landMatcher)
      this
    }
  }

  sealed class TargetMagnet(val value: ObjectOrPlayer)
  object TargetMagnet {
    implicit def fromCardDefinition(cardDefinition: CardDefinition)(implicit gameObjectState: GameObjectState) = new TargetMagnet(gameObjectState.getCard(cardDefinition).objectId)
    implicit def fromPlayer(playerId: PlayerId) = new TargetMagnet(playerId)
  }

  class TargetChoiceMatcher extends Matcher[GameAction] {
    private val baseMatcher: Matcher[GameAction] = beAnInstanceOf[TargetChoice]
    private val otherMatchers: ListBuffer[Matcher[TargetChoice]] = ListBuffer()
    private def finalMatcher: Matcher[GameAction] = otherMatchers.foldLeft(baseMatcher)((m1, m2) => m1.and(m2 ^^ {(_: GameAction).asInstanceOf[TargetChoice]}))

    override def apply[S <: GameAction](t: Expectable[S]): MatchResult[S] = finalMatcher.apply(t)

    def forPlayer(playerIdentifier: PlayerId): TargetChoiceMatcher = {
      otherMatchers.addOne(((_: TargetChoice).playerToAct) ^^ beTypedEqualTo(playerIdentifier))
      this
    }
    def withAvailableTargets(targetMagnets: TargetMagnet*): TargetChoiceMatcher = {
      otherMatchers.addOne(((_: TargetChoice).validOptions) ^^ contain(exactly(targetMagnets.map(_.value): _*)))
      this
    }
  }

  def bePriorityChoice: PriorityChoiceMatcher = new PriorityChoiceMatcher
  def beTargetChoice: TargetChoiceMatcher = new TargetChoiceMatcher
  def beEffectChoice[T <: OneShotEffectChoice : ClassTag](m: Matcher[T]): Matcher[GameAction] = beAnInstanceOf[ResolveEffectChoice] and
    ((_: GameAction).asInstanceOf[ResolveEffectChoice].effectChoice) ^^ (beAnInstanceOf[T] and (((_: OneShotEffectChoice).asInstanceOf[T]) ^^ m))
}
