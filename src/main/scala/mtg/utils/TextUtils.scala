package mtg.utils

object TextUtils {
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
