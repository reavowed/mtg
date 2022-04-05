package mtg.actions

import mtg.core.{ObjectId, PlayerId}
import mtg.game.objects.{CopyOfSpell, StackObject}
import mtg.game.state.{GameActionResult, GameState, GameObjectAction}

case class CopySpellAction(playerId: PlayerId, spellId: ObjectId) extends GameObjectAction {
  override def execute(gameState: GameState): GameActionResult = {
    for {
      spell <- gameState.gameObjectState.stack.find(_.objectId == spellId)
    } yield {
      // TODO: Take into account additional copiable characteristics as per 707.2.
      val characteristics = spell.underlyingObject.baseCharacteristics
      val underlyingObject = CopyOfSpell(characteristics, playerId)
      gameState.gameObjectState.addObjectToStack(StackObject(underlyingObject, _, playerId, spell.chosenModes, spell.targets, Map.empty))
    }
  }
  override def canBeReverted: Boolean = true
}
