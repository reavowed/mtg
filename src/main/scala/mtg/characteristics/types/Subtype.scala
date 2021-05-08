package mtg.characteristics.types

import mtg.characteristics.Color

sealed class Subtype

sealed class LandType extends Subtype

sealed class BasicLandType(val color: Color) extends LandType

object BasicLandType {
  val Plains = new BasicLandType(Color.White)
  val Island = new BasicLandType(Color.Blue)
  val Swamp = new BasicLandType(Color.Black)
  val Mountain = new BasicLandType(Color.Red)
  val Forest = new BasicLandType(Color.Green)
}
