package mtg.utils

case class NounPhrase(singular: String, plural: String) {
  def withNumber(number: Int): String = {
    val nounWord = if (number == 1) singular else plural
    val numberWord = TextUtils.getNumberWord(number, nounWord)
    numberWord + " " + nounWord
  }
  def withPrefix(prefix: String): NounPhrase = {
    NounPhrase(prefix + " " + singular, prefix + " " + plural)
  }
}

object NounPhrase {
  def apply(singular: String): NounPhrase = NounPhrase(singular, singular + "s")
}
