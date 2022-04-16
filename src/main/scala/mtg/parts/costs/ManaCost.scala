package mtg.parts.costs

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.{JsonSerializer, SerializerProvider}
import mtg.core.symbols.ManaSymbol
import mtg.game.state.{GameAction, ObjectWithState}
import mtg.stack.adding.PayManaCosts

@JsonSerialize(using = classOf[ManaCost.Serializer])
case class ManaCost(symbols: ManaSymbol*) extends Cost {
  override def text: String = symbols.map(_.text).mkString
  override def isUnpayable(objectWithAbility: ObjectWithState): Boolean = false
  override def payForAbility(objectWithAbility: ObjectWithState): GameAction[Any] = PayManaCosts(this, objectWithAbility.controllerOrOwner) // TODO: Controller of ability should pay
  def manaValue: Int = symbols.map(_.manaValue).sum
}

object ManaCost {
  class Serializer extends JsonSerializer[ManaCost] {
    override def serialize(value: ManaCost, gen: JsonGenerator, serializers: SerializerProvider): Unit = {
      gen.writeString(value.symbols.map(_.text).mkString)
    }
  }
}
