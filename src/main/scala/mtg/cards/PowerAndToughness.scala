package mtg.cards

sealed abstract class PowerAndToughness {
  def basePower: Int
  def baseToughness: Int
}
object PowerAndToughness {
  case class Fixed(basePower: Int, baseToughness: Int) extends PowerAndToughness
}
