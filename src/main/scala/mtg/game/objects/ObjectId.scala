package mtg.game.objects

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.{JsonSerializer, SerializerProvider}
import mtg.game.state.{Characteristics, GameState}

@JsonSerialize(using = classOf[ObjectId.Serializer])
case class ObjectId(sequentialId: Int) {
  override def toString: String = sequentialId.toString
  def currentCharacteristics(gameState: GameState): Characteristics = gameState.derivedState.objectStates(this).characteristics

  def getPower(gameState: GameState): Int = gameState.derivedState.objectStates(this).characteristics.power.getOrElse(0)
  def getToughness(gameState: GameState): Int = gameState.derivedState.objectStates(this).characteristics.toughness.getOrElse(0)
  def getMarkedDamage(gameState: GameState): Int = gameState.derivedState.objectStates(this).gameObject.markedDamage
}

object ObjectId {
  class Serializer extends JsonSerializer[ObjectId] {
    override def serialize(value: ObjectId, gen: JsonGenerator, serializers: SerializerProvider): Unit = {
      gen.writeNumber(value.sequentialId)
    }
  }
}
