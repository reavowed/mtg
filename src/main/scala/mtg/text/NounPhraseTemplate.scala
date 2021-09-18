package mtg.text

trait NounPhraseTemplate {
  def singular: String
  def plural: String
  def toString(grammaticalNumber: GrammaticalNumber): String = grammaticalNumber match {
    case GrammaticalNumber.Singular => singular
    case GrammaticalNumber.Plural => plural
  }
  def withPrefix(prefix: String) = NounPhraseTemplate.Prefixed(prefix, this)
  def withSuffix(suffix: String) = NounPhraseTemplate.Suffixed(this, suffix)
}

object NounPhraseTemplate {
  case class Simple(singular: String, plural: String) extends NounPhraseTemplate

  object Simple {
    def apply(singular: String): Simple = {
      Simple(singular, None)
    }
    def apply(singular: String, plural: Option[String]): Simple = {
      Simple(singular, plural.getOrElse(singular + "s"))
    }
  }

  case class Compound(children: Seq[NounPhraseTemplate], joiner: String) extends NounPhraseTemplate {
    override def singular: String = join(_.singular)
    override def plural: String = join(_.plural)

    private def join(f: NounPhraseTemplate => String): String = {
      children.map(f).mkString(" " + joiner + " ")
    }
  }

  case class Prefixed(prefix: String, child: NounPhraseTemplate) extends NounPhraseTemplate {
    override def singular: String = prefix + " " + child.singular
    override def plural: String = prefix + " " + child.plural
  }

  case class Suffixed(child: NounPhraseTemplate, suffix: String) extends NounPhraseTemplate {
    override def singular: String = child.singular + " " + suffix
    override def plural: String = child.plural + " " + suffix
  }
}
