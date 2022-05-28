package mtg.instructions.nounPhrases

import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.instructions.grammar.{GrammaticalNumber, GrammaticalPerson}
import mtg.instructions.nouns.Noun
import mtg.parts.counters.CounterType
import mtg.utils.TextUtils._

case class CountersLiteral(numberOfCounters: Int, counterType: CounterType) extends SingleIdentifyingNounPhrase[Map[CounterType, Int]] {
  override def identifySingle(gameState: GameState, resolutionContext: StackObjectResolutionContext): (Map[CounterType, Int], StackObjectResolutionContext) = {
    (Map(counterType -> numberOfCounters), resolutionContext)
  }
  override def number: GrammaticalNumber = GrammaticalNumber(numberOfCounters)
  override def person: GrammaticalPerson = GrammaticalPerson.Third
  override def getText(cardName: String): String = (counterType.description + " " + Noun.Counter.getText(cardName, number)).withNumberWord(numberOfCounters)
}
