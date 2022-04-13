package mtg.helpers

import mtg.cards.CardDefinition
import mtg.core.{ObjectOrPlayerId, PlayerId}
import mtg.game.objects.{Card, GameObject, GameObjectState}
import mtg.game.priority.PriorityChoice
import mtg.game.priority.actions.{ActivateAbilityAction, CastSpellAction, PlayLandAction}
import mtg.game.state.GameAction
import mtg.instructions.InstructionChoice
import mtg.stack.adding.TargetChoice
import mtg.stack.resolving.ResolveInstructionChoice
import org.specs2.matcher.{Expectable, MatchResult, Matcher}
import org.specs2.mutable.SpecificationLike

import scala.collection.mutable.ListBuffer
import scala.reflect.ClassTag

trait GameUpdateHelpers extends SpecificationLike with GameObjectStateHelpers {
  def beCastSpellAction(cardDefinition: CardDefinition): Matcher[CastSpellAction] = beCardObject(cardDefinition) ^^ ((_: CastSpellAction).objectToCast.gameObject)
  def beCastSpellAction(gameObject: GameObject): Matcher[CastSpellAction] = { (castSpellAction: CastSpellAction) =>
    (castSpellAction.objectToCast.gameObject == gameObject, "was given object", "was not given object")
  }

  class PriorityChoiceMatcher extends Matcher[GameAction[_]] {
    private val baseMatcher: Matcher[GameAction[_]] = beAnInstanceOf[PriorityChoice]
    private val otherMatchers: ListBuffer[Matcher[PriorityChoice]] = ListBuffer()
    private def finalMatcher: Matcher[GameAction[_]] = otherMatchers.foldLeft(baseMatcher)((m1, m2) => m1.and(m2 ^^ {(_: GameAction[_]).asInstanceOf[PriorityChoice]}))

    override def apply[S <: GameAction[_]](t: Expectable[S]): MatchResult[S] = finalMatcher.apply(t)

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
    def withNoAvailableAbilities: PriorityChoiceMatcher = {
      otherMatchers.addOne(((_: PriorityChoice).availableActions.ofType[ActivateAbilityAction]) ^^ beEmpty)
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

    def withAvailableLands(landMatcher: Matcher[Seq[PlayLandAction]]): PriorityChoiceMatcher = {
      otherMatchers.addOne(((_: PriorityChoice).availableActions.ofType[PlayLandAction]) ^^ landMatcher)
      this
    }
  }

  sealed class TargetMagnet(val value: ObjectOrPlayerId)
  object TargetMagnet {
    implicit def fromCardDefinition(cardDefinition: CardDefinition)(implicit gameObjectState: GameObjectState) = new TargetMagnet(gameObjectState.getCard(cardDefinition).objectId)
    implicit def fromPlayer(playerId: PlayerId) = new TargetMagnet(playerId)
  }

  class TargetChoiceMatcher extends Matcher[GameAction[_]] {
    private val baseMatcher: Matcher[GameAction[_]] = beAnInstanceOf[TargetChoice]
    private val otherMatchers: ListBuffer[Matcher[TargetChoice]] = ListBuffer()
    private def finalMatcher: Matcher[GameAction[_]] = otherMatchers.foldLeft(baseMatcher)((m1, m2) => m1.and(m2 ^^ {(_: GameAction[_]).asInstanceOf[TargetChoice]}))

    override def apply[S <: GameAction[_]](t: Expectable[S]): MatchResult[S] = finalMatcher.apply(t)

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
  def beInstructionChoice[T <: InstructionChoice : ClassTag](m: Matcher[T]): Matcher[GameAction[_]] = beAnInstanceOf[ResolveInstructionChoice] and
    ((_: GameAction[_]).asInstanceOf[ResolveInstructionChoice].instructionChoice) ^^ (beAnInstanceOf[T] and (((_: InstructionChoice).asInstanceOf[T]) ^^ m))


  def beActivatableAbilityActionForCard(cardDefinition: CardDefinition): Matcher[ActivateAbilityAction] = {
    ((_: ActivateAbilityAction).objectWithAbility.gameObject.underlyingObject.asOptionalInstanceOf[Card].map(_.printing.cardDefinition)) ^^ beSome(cardDefinition)
  }
}
