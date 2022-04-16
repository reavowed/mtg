package mtg.abilities.builder

import mtg.instructions.numbers.{ConstantOrGreaterMatcher, ConstantOrLessMatcher, NumberPhrase}

trait NumberBuilder {
  implicit class IntExtensions(number: Int) {
    def orGreater: NumberPhrase = ConstantOrGreaterMatcher(number)
    def orLess: NumberPhrase = ConstantOrLessMatcher(number)
  }
}
