package mtg.game.`object`

case class ObjectId(sequentialId: Int) {
  override def toString: String = sequentialId.toString
}
