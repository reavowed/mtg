package mtg.utils

object TextUtils {
  implicit class StringSeqExtensions(seq: Seq[String]) {
    def toCommaList(joiner: String, oxfordComma: Boolean = false) = seq.length match {
      case 0 => ""
      case 1 => seq.head
      case _ => seq.init.mkString(", ") + (if (oxfordComma) ", " else " ") + joiner + " " + seq.last
    }
  }
  implicit class StringExtensions(str: String) {
    def withArticle: String = {
      getArticle(str) + " " + str
    }
    def uncapitalize: String = {
      s"${str.head.toLower}${str.tail}"
    }
  }
  def getArticle(word: String): String = {
      if ("aeiou".contains(word.head)) "an" else "a"
  }
}
