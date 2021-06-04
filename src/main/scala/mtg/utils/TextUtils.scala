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
  }
  def getArticle(word: String): String = {
      if ("aeiou".contains(word.head)) "an" else "a"
  }

  def getWord(number: Int, followingWord: String): String = number match {
    case 1 => getArticle(followingWord)
    case 2 => "two"
    case n => n.toString
  }
}
