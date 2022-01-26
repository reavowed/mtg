package mtg.stack.adding

import mtg.effects.EffectContext
import mtg.effects.targets.TargetIdentifier
import mtg.game.state._
import mtg.game.{ObjectId, ObjectOrPlayer, PlayerId}

case class ChooseTargets(stackObjectId: ObjectId) extends ExecutableGameAction[Unit] {
  override def execute()(implicit gameState: GameState): PartialGameActionResult[Unit] = {
    val stackObjectWithState = gameState.gameObjectState.derivedState.stackObjectStates(stackObjectId)
    val targetIdentifiers = TargetIdentifier.getAll(stackObjectWithState)
    chooseTargets(targetIdentifiers)
  }
  private def chooseTargets(targetIdentifiers: Seq[TargetIdentifier[_]])(implicit gameState: GameState): PartialGameActionResult[Unit] = {
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
  private def addTarget(remainingTargetIdentifiers: Seq[TargetIdentifier[_]])(objectOrPlayer: ObjectOrPlayer, gameState: GameState): PartialGameActionResult[Unit] = {
    PartialGameActionResult.ChildWithCallback(
      WrappedOldUpdates(AddTarget(stackObjectId, objectOrPlayer)),
      (_: Any, gameState) => chooseTargets(remainingTargetIdentifiers)(gameState))
  }
}

case class TargetChoice(playerToAct: PlayerId, objectId: ObjectId, targetDescription: String, validOptions: Seq[ObjectOrPlayer]) extends DirectChoice[ObjectOrPlayer] {
  def handleDecision(serializedDecision: String)(implicit gameState: GameState): Option[ObjectOrPlayer] = {
    validOptions.find(_.toString == serializedDecision)
  }
}
object TargetChoice {
  def apply(stackObjectWithState: StackObjectWithState, targetIdentifier: TargetIdentifier[_])(implicit gameState: GameState): TargetChoice = {
    TargetChoice(
      stackObjectWithState.controller,
      stackObjectWithState.gameObject.objectId,
      targetIdentifier.getText(stackObjectWithState.gameObject.underlyingObject.getSourceName(gameState)),
      targetIdentifier.getValidChoices(stackObjectWithState, gameState, EffectContext(stackObjectWithState, gameState)))
  }
}

case class AddTarget(stackObjectId: ObjectId, target: ObjectOrPlayer) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.updateStackObject(stackObjectId, _.addTarget(target))
  }
  override def canBeReverted: Boolean = true
}
