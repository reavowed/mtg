package mtg.game.objects

case class ObjectId(sequentialId: Int) {
  override def toString: String = sequentialId.toString
}
