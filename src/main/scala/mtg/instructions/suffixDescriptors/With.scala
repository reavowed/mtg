package mtg.instructions.suffixDescriptors

import mtg.actions.moveZone.MoveToBattlefieldAction
import mtg.effects.EffectContext
import mtg.instructions.EntersTheBattlefieldModifier
import mtg.instructions.nounPhrases.{CountersLiteral, StaticSingleIdentifyingNounPhrase}
import mtg.parts.counters.CounterType
import mtg.utils.MapUtils._

case class With(countersPhrase: StaticSingleIdentifyingNounPhrase[Map[CounterType, Int]]) extends EntersTheBattlefieldModifier {
  override def getText(cardName: String): String = s"with ${countersPhrase.getText(cardName)} on it"
  override def modifyAction(action: MoveToBattlefieldAction, effectContext: EffectContext): MoveToBattlefieldAction = {
    val newCounters = countersPhrase.identify(effectContext)
    action.copy(counters = action.counters.add(newCounters))
  }
}

object With {
  def apply(number: Int, kind: CounterType): With = {
    With(CountersLiteral(number, kind))
  }
}
