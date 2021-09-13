package mtg.game.actions

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.{JsonSerializer, SerializerProvider}
import mtg.game.actions.cast.CastSpellAction
import mtg.game.state.{BackupAction, GameState, InternalGameAction}
import mtg.game.{ObjectId, PlayerId}

@JsonSerialize(using = classOf[PriorityAction.Serializer])
abstract class PriorityAction extends InternalGameAction {
  def objectId: ObjectId
  def displayText: String
  def optionText: String
}

object PriorityAction {
  def getAll(player: PlayerId, gameState: GameState, backupAction: BackupAction): Seq[PriorityAction] = {
    CastSpellAction.getCastableSpells(player, gameState, backupAction) ++
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
