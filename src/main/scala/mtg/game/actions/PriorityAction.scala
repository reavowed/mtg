package mtg.game.actions

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.{JsonSerializer, SerializerProvider}
import mtg.game.PlayerIdentifier
import mtg.game.objects.ObjectId
import mtg.game.state.{GameState, InternalGameAction}

@JsonSerialize(using = classOf[PriorityAction.Serializer])
abstract class PriorityAction extends InternalGameAction {
  def objectId: ObjectId
  def displayText: String
  def optionText: String
}

object PriorityAction {
  def getAll(player: PlayerIdentifier, gameState: GameState): Seq[PriorityAction] = {
    ActivateAbilityAction.getActivatableAbilities(player, gameState) ++
      PlayLandAction.getPlayableLands(player, gameState)
  }

  case class Wrapper(objectId: ObjectId, displayText: String, optionText: String)
  class Serializer extends JsonSerializer[PriorityAction] {
    override def serialize(value: PriorityAction, gen: JsonGenerator, serializers: SerializerProvider): Unit = {
      gen.writeObject(Wrapper(value.objectId, value.displayText, value.optionText))
    }
  }
}
