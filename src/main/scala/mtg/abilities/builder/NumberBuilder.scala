package mtg.abilities.builder

import mtg.instructions.numbers.{ConstantOrGreaterMatcher, NumberPhrase}

trait NumberBuilder {
  implicit class IntExtensions(number: Int) {
    def orGreater: NumberPhrase = ConstantOrGreaterMatcher(number)
  }
}
