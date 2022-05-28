package mtg.instructions.suffixDescriptors

import mtg.actions.moveZone.MoveToBattlefieldAction
import mtg.effects.EffectContext
import mtg.instructions.EntersTheBattlefieldModifier
import mtg.instructions.nounPhrases.{CountersLiteral, StaticSingleIdentifyingNounPhrase}
import mtg.parts.Counter
import mtg.utils.MapUtils._

case class With(countersPhrase: StaticSingleIdentifyingNounPhrase[Map[Counter, Int]]) extends EntersTheBattlefieldModifier {
  override def getText(cardName: String): String = s"with ${countersPhrase.getText(cardName)} on it"
  override def modifyAction(action: MoveToBattlefieldAction, effectContext: EffectContext): MoveToBattlefieldAction = {
    val newCounters = countersPhrase.identify(effectContext)
    action.copy(counters = action.counters.add(newCounters))
  }
}

object With {
  def apply(number: Int, kind: Counter): With = {
    With(CountersLiteral(number, kind))
  }
}
