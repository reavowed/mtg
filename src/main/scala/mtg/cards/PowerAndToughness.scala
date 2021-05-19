package mtg.cards

sealed class PowerAndToughness
object PowerAndToughness {
  case class Fixed(power: Int, toughness: Int) extends PowerAndToughness
}
