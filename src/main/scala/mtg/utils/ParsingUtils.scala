package mtg.utils

import mtg.game.objects.ObjectId

object ParsingUtils {
  def splitStringBySpaces(string: String): Seq[String] = {
    string.split(" ").toSeq.filter(_.nonEmpty)
  }
  def splitStringAsIds(string: String): Option[Seq[ObjectId]] = {
    splitStringBySpaces(string).map(_.toIntOption.map(ObjectId(_))).swap
  }
}
