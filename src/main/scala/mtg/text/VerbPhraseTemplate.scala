package mtg.text

import mtg.utils.TextUtils._

trait VerbPhraseTemplate {
  def singular: String
  def plural: String
  def toString(grammaticalNumber: GrammaticalNumber): String = grammaticalNumber match {
    case GrammaticalNumber.Singular => singular
    case GrammaticalNumber.Plural => plural
  }
  def withSuffix(suffix: String) = VerbPhraseTemplate.Suffixed(this, suffix)
}

object VerbPhraseTemplate {
  case class Simple(singular: String, plural: String) extends VerbPhraseTemplate
  object Simple {
    def apply(plural: String): Simple = {
      Simple(plural + "s", plural)
    }
  }
  case class Suffixed(child: VerbPhraseTemplate, suffix: String) extends VerbPhraseTemplate {
    override def singular: String = child.singular + " " + suffix
    override def plural: String = child.plural + " " + suffix
  }

  case class List(children: Seq[VerbPhraseTemplate]) extends VerbPhraseTemplate {
    def singular: String = join(_.singular)
    def plural: String = join(_.plural)

    private def join(f: VerbPhraseTemplate => String): String = {
      children.map(f).toCommaList
    }
  }
}
