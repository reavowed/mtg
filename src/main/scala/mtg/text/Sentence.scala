package mtg.text

sealed trait Sentence {
  def text: String
}

object Sentence {
  case class NounAndVerb(nounPhrase: NounPhrase, verbPhraseTemplate: VerbPhraseTemplate) extends Sentence {
    override def text: String = nounPhrase.text + " " + verbPhraseTemplate.toString(nounPhrase.number)
  }
}
