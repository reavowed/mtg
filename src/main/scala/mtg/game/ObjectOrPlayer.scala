package mtg.game

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.{JsonSerializer, SerializerProvider}
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import mtg.game.state.{Characteristics, GameState}

sealed trait ObjectOrPlayer

@JsonSerialize(using = classOf[ObjectId.Serializer])
case class ObjectId(sequentialId: Int) extends ObjectOrPlayer {
  override def toString: String = sequentialId.toString
  def currentCharacteristics(gameState: GameState): Characteristics = gameState.gameObjectState.derivedState.allObjectStates(this).characteristics

  def getName(gameState: GameState): String = currentCharacteristics(gameState).name
  def getPower(gameState: GameState): Int = currentCharacteristics(gameState).power.getOrElse(0)
  def getToughness(gameState: GameState): Int = currentCharacteristics(gameState).toughness.getOrElse(0)
  def getMarkedDamage(gameState: GameState): Int = gameState.gameObjectState.derivedState.permanentStates(this).gameObject.markedDamage
}

object ObjectId {
  class Serializer extends JsonSerializer[ObjectId] {
    override def serialize(value: ObjectId, gen: JsonGenerator, serializers: SerializerProvider): Unit = {
      gen.writeNumber(value.sequentialId)
    }
  }
}

@JsonSerialize(using = classOf[PlayerId.Serializer])
case class PlayerId(id: String) extends ObjectOrPlayer {
  override def toString: String = id
}
object PlayerId {
  class Serializer extends JsonSerializer[PlayerId] {
    override def serialize(value: PlayerId, gen: JsonGenerator, serializers: SerializerProvider): Unit = gen.writeString(value.id)
  }
}
