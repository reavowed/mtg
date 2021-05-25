package mtg.game.objects

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.{JsonSerializer, SerializerProvider}
import mtg.game.state.{Characteristics, GameState}

@JsonSerialize(using = classOf[ObjectId.Serializer])
case class ObjectId(sequentialId: Int) {
  override def toString: String = sequentialId.toString
  def currentCharacteristics(gameState: GameState): Characteristics = gameState.derivedState.objectStates(this).characteristics
}

object ObjectId {
  class Serializer extends JsonSerializer[ObjectId] {
    override def serialize(value: ObjectId, gen: JsonGenerator, serializers: SerializerProvider): Unit = {
      gen.writeNumber(value.sequentialId)
    }
  }
}
