package mtg.utils

object TextUtils {
  implicit class StringSeqExtensions(seq: Seq[String]) {
    def toCommaList: String = seq.length match {
      case 0 => ""
      case 1 => seq.head
      case _ => seq.init.mkString(", ") + " and " + seq.last
    }
  }
  implicit class StringExtensions(str: String) {
    def withArticle: String = {
      getArticle(str) + " " + str
    }
    def uncapitalize: String = {
      str.head.toLower + str.tail
    }
  }
  def getArticle(word: String): String = {
      if ("aeiou".contains(word.head)) "an" else "a"
  }

  def getNumberWord(number: Int, followingWord: String): String = number match {
    case 1 => getArticle(followingWord)
    case 2 => "two"
    case n => n.toString
  }
  def getSingularOrPlural(number: Int, singular: String): String = {
    getSingularOrPlural(number, singular, singular + "s")
  }
  def getSingularOrPlural(number: Int, singular: String, plural: String): String = {
    if (number == 1) singular else plural
  }
}
