package mtg.text

object NounPhrases {
  object You extends NounPhrase {
    override def text: String = "you"
    override def possessiveText: String = "your"
    override def number: GrammaticalNumber = GrammaticalNumber.Plural
  }
  object It extends NounPhrase {
    override def text: String = "it"
    override def possessiveText: String = "its"
    override def number: GrammaticalNumber = GrammaticalNumber.Singular
  }
}
