package mtg.game.state

case class PermanentStatus(
  isTapped: Boolean,
  isFlipped: Boolean,
  isFaceDown: Boolean,
  isPhasedOut: Boolean)
{
  def tap(): PermanentStatus = copy(isTapped = true)
}
