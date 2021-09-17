package mtg.abilities.builder

import mtg.effects.numbers.{ConstantOrGreaterMatcher, NumberMatcher}

trait NumberBuilder {
  implicit class IntExtensions(number: Int) {
    def orGreater: NumberMatcher = ConstantOrGreaterMatcher(number)
  }
}
