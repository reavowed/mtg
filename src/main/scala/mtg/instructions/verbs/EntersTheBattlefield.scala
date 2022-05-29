package mtg.instructions.verbs

import mtg.actions.moveZone.MoveToBattlefieldAction
import mtg.continuousEffects.{ContinuousEffect, ReplacementEffect}
import mtg.core.ObjectId
import mtg.effects.{EffectContext, InstructionResolutionContext}
import mtg.game.state.history.HistoryEvent
import mtg.game.state.history.HistoryEvent.ResolvedAction
import mtg.game.state.{DirectGameObjectAction, GameState}
import mtg.instructions.grammar.VerbInflection
import mtg.instructions.nounPhrases.{IndefiniteNounPhrase, StaticSingleIdentifyingNounPhrase}
import mtg.instructions.{EntersTheBattlefieldModifier, IntransitiveEventMatchingVerb, IntransitiveStaticAbilityVerb, Verb}

object EntersTheBattlefield extends Verb.WithSuffix(Verb.Enter, "the battlefield") with IntransitiveEventMatchingVerb.Simple[ObjectId] {
  override def matchSubject(eventToMatch: ResolvedAction[_], gameState: GameState, context: InstructionResolutionContext): Option[(ObjectId, InstructionResolutionContext)] = eventToMatch match {
    case ResolvedAction(MoveToBattlefieldAction(_, _, _), Some(objectId: ObjectId), _) => Some((objectId, context))
    case _ => None
  }

  def apply(modifier: EntersTheBattlefieldModifier): WithModifier = WithModifier(modifier)

  case class WithModifier(modifier: EntersTheBattlefieldModifier)
    extends IntransitiveStaticAbilityVerb[ObjectId]
  {
    override def inflect(verbInflection: VerbInflection, cardName: String): String = {
      Verb.Enter.inflect(verbInflection, cardName) + " the battlefield " + modifier.getText(cardName)
    }
    override def getEffects(subjectPhrase: StaticSingleIdentifyingNounPhrase[ObjectId], effectContext: EffectContext): Seq[ContinuousEffect] = {
      Seq(EntersTheBattlefieldReplacementEffect(subjectPhrase, modifier)(effectContext))
    }
  }
}

case class EntersTheBattlefieldReplacementEffect(
    subjectPhrase: StaticSingleIdentifyingNounPhrase[ObjectId],
    modifier: EntersTheBattlefieldModifier)(
    effectContext: EffectContext) extends ReplacementEffect
{
  val expectedObjectId = subjectPhrase.identify(effectContext)
  override def replaceAction(action: DirectGameObjectAction[_]): Option[DirectGameObjectAction[_]] = action match {
    case action @ MoveToBattlefieldAction(`expectedObjectId`, _, _) =>
      Some(modifier.modifyAction(action, effectContext))
    case _ =>
      None
  }
}
