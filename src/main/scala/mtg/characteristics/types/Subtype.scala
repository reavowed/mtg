package mtg.characteristics.types

import mtg.characteristics.Color

sealed class Subtype

sealed class LandType extends Subtype
sealed class CreatureType(val name: String) extends Subtype

sealed class BasicLandType(val name: String, val color: Color) extends LandType

object BasicLandType {
  val Plains = new BasicLandType("Plains", Color.White)
  val Island = new BasicLandType("Island", Color.Blue)
  val Swamp = new BasicLandType("Swamp", Color.Black)
  val Mountain = new BasicLandType("Mountain", Color.Red)
  val Forest = new BasicLandType("Forest", Color.Green)
}

object CreatureType {
  val Soldier = new CreatureType("Soldier")
  val Spirit = new CreatureType("Spirit")
}
