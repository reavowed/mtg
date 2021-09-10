package mtg.parts.counters

import mtg.utils.NounPhrase

abstract class CounterType {
  def description: String
  def nounPhrase: NounPhrase = NounPhrase("counter").withPrefix(description)
}
