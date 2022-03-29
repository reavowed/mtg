package mtg.stack.adding

import mtg.core.{ObjectId, ObjectOrPlayerId, PlayerId}
import mtg.effects.EffectContext
import mtg.effects.targets.Target
import mtg.game.state._

case class ChooseTargets(stackObjectId: ObjectId) extends ExecutableGameAction[Unit] {
  override def execute()(implicit gameState: GameState): PartialGameActionResult[Unit] = {
    val stackObjectWithState = gameState.gameObjectState.derivedState.stackObjectStates(stackObjectId)
    val targetIdentifiers = Target.getAll(stackObjectWithState)
    chooseTargets(targetIdentifiers)
  }
  private def chooseTargets(targetIdentifiers: Seq[Target[_]])(implicit gameState: GameState): PartialGameActionResult[Unit] = {
    val stackObjectWithState = gameState.gameObjectState.derivedState.stackObjectStates(stackObjectId)
    targetIdentifiers match {
      case nextTargetIdentifier +: remainingTargetIdentifiers =>
        PartialGameActionResult.ChildWithCallback(
          TargetChoice(stackObjectWithState, nextTargetIdentifier),
          addTarget(remainingTargetIdentifiers))
      case Nil =>
        PartialGameActionResult.Value(())
    }
  }
  private def addTarget(remainingTargetIdentifiers: Seq[Target[_]])(objectOrPlayer: ObjectOrPlayerId, gameState: GameState): PartialGameActionResult[Unit] = {
    PartialGameActionResult.ChildWithCallback(
      WrappedOldUpdates(AddTarget(stackObjectId, objectOrPlayer)),
      (_: Any, gameState) => chooseTargets(remainingTargetIdentifiers)(gameState))
  }
}

case class TargetChoice(playerToAct: PlayerId, objectId: ObjectId, targetDescription: String, validOptions: Seq[ObjectOrPlayerId]) extends Choice[ObjectOrPlayerId] {
  def handleDecision(serializedDecision: String)(implicit gameState: GameState): Option[ObjectOrPlayerId] = {
    validOptions.find(_.toString == serializedDecision)
  }
}
object TargetChoice {
  def apply(stackObjectWithState: StackObjectWithState, targetIdentifier: Target[_])(implicit gameState: GameState): TargetChoice = {
    TargetChoice(
      stackObjectWithState.controller,
      stackObjectWithState.gameObject.objectId,
      targetIdentifier.getText(CurrentCharacteristics.getName(stackObjectWithState)),
      targetIdentifier.getValidChoices(stackObjectWithState, gameState, EffectContext(stackObjectWithState)))
  }
}

case class AddTarget(stackObjectId: ObjectId, target: ObjectOrPlayerId) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.updateStackObject(stackObjectId, _.addTarget(target))
  }
  override def canBeReverted: Boolean = true
}
