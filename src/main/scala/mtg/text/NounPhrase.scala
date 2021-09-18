package mtg.text

trait NounPhrase {
  def text: String
  def possessiveText: String = text + "'s"
  def number: GrammaticalNumber
}
object NounPhrase {
  case class Simple(text: String, number: GrammaticalNumber) extends NounPhrase
  case class Templated(template: NounPhraseTemplate, number: GrammaticalNumber) extends NounPhrase {
    def text: String = template.toString(number)
  }
}
