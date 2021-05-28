package mtg.utils

object TextUtils {
  implicit class StringExtensions(str: String) {
    def withArticle: String = {
      if ("aeiou".contains(str.head)) {
        "an " + str
      } else {
        "a " + str
      }
    }
  }
}
