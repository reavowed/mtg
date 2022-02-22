package mtg.game.priority.actions

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.{JsonSerializer, SerializerProvider}
import mtg.core.{ObjectId, PlayerId}
import mtg.game.state.{ExecutableGameAction, GameState}

@JsonSerialize(using = classOf[PriorityAction.Serializer])
abstract class PriorityAction extends ExecutableGameAction[Unit] {
  def objectId: ObjectId
  def displayText: String
  def optionText: String
}

object PriorityAction {
  def getAll(player: PlayerId, gameState: GameState): Seq[PriorityAction] = {
    CastSpellAction.getCastableSpells(player, gameState) ++
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
