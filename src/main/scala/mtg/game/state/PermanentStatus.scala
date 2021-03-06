package mtg.game.state

case class PermanentStatus(
  isTapped: Boolean,
  isFlipped: Boolean,
  isFaceDown: Boolean,
  isPhasedOut: Boolean)
{
  def tap(): PermanentStatus = copy(isTapped = true)
  def untap(): PermanentStatus = copy(isTapped = false)
}

object PermanentStatus {
  val Default: PermanentStatus = PermanentStatus(isTapped = false, isFlipped = false, isFaceDown = false, isPhasedOut = false)
}
