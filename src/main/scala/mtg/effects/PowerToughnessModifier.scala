package mtg.effects

import mtg.game.state.Characteristics

case class PowerToughnessModifier(powerModifier: Int, toughnessModifier: Int) {
  def description: String = {
    def getDescription(modifier: Int) = if (modifier >= 0) "+" + modifier else modifier.toString
    getDescription(powerModifier) + "/" + getDescription(toughnessModifier)
  }
  def applyToCharacteristics(characteristics: Characteristics): Characteristics = {
    characteristics.copy(power = characteristics.power.map(_ + powerModifier), toughness = characteristics.toughness.map(_ + toughnessModifier))
  }
}
object PowerToughnessModifier {
  implicit def fromTuple(tuple: (Int, Int)): PowerToughnessModifier = (PowerToughnessModifier.apply _).tupled(tuple)
}
