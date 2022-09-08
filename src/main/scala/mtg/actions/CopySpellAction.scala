package mtg.actions

import mtg.definitions.{ObjectId, PlayerId}
import mtg.game.objects.{CopyOfSpell, StackObject}
import mtg.game.state.{DirectGameObjectAction, GameState}

case class CopySpellAction(playerId: PlayerId, spellId: ObjectId) extends DirectGameObjectAction[ObjectId] {
  override def execute(implicit gameState: GameState): DirectGameObjectAction.Result[ObjectId] = {
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
