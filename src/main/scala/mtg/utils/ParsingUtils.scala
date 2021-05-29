package mtg.utils

import mtg._
import mtg.game.ObjectId

object ParsingUtils {
  def splitStringBySpaces(string: String): Seq[String] = {
    string.split(" ").toSeq.filter(_.nonEmpty)
  }
  def splitStringAsInts(string: String): Option[Seq[Int]] = {
    splitStringBySpaces(string).map(_.toIntOption).swap
  }
  def splitStringAsIds(string: String): Option[Seq[ObjectId]] = {
    splitStringAsInts(string).map(_.map(ObjectId(_)))
  }
}
