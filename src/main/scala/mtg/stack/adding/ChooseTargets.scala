package mtg.stack.adding

import mtg.effects.EffectContext
import mtg.effects.targets.TargetIdentifier
import mtg.game.state._
import mtg.game.{ObjectId, ObjectOrPlayer, PlayerId}

case class ChooseTargets(objectId: ObjectId, backupAction: BackupAction) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.derivedState.stackObjectStates.get(objectId).toSeq.flatMap { stackObjectWithState =>
      val targetIdentifiers = TargetIdentifier.getAll(stackObjectWithState)
      targetIdentifiers.map(targetIdentifier => TargetChoice(
        stackObjectWithState.controller,
        objectId,
        targetIdentifier.getText(stackObjectWithState.gameObject.underlyingObject.getSourceName(gameState)),
        targetIdentifier.getValidChoices(stackObjectWithState, gameState, EffectContext(stackObjectWithState, gameState))))
    }
  }
  override def canBeReverted: Boolean = true
}

case class TargetChoice(playerToAct: PlayerId, objectId: ObjectId, targetDescription: String, validOptions: Seq[ObjectOrPlayer]) extends Choice {
  override def parseDecision(serializedChosenOption: String): Option[Decision] = {
    validOptions.find(_.toString == serializedChosenOption)
      .map(AddTarget(objectId, _))
  }
}

case class AddTarget(stackObjectId: ObjectId, target: ObjectOrPlayer) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.updateStackObject(stackObjectId, _.addTarget(target))
  }
  override def canBeReverted: Boolean = true
}
