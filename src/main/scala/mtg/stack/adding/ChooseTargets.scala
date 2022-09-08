package mtg.stack.adding

import mtg.actions.stack.AddTarget
import mtg.definitions.{ObjectId, ObjectOrPlayerId, PlayerId}
import mtg.effects.EffectContext
import mtg.game.state._
import mtg.instructions.nounPhrases.Target

case class ChooseTargets(stackObjectId: ObjectId) extends DelegatingGameAction[Unit] {
  override def delegate(implicit gameState: GameState): GameAction[Unit] = {
    val stackObjectWithState = gameState.gameObjectState.derivedState.stackObjectStates(stackObjectId)
    val targetIdentifiers = Target.getAll(stackObjectWithState)
    targetIdentifiers.map(chooseTarget).traverse
  }

  def chooseTarget(targetIdentifier: Target[_])(implicit gameState: GameState): GameAction[_] = {
    val stackObjectWithState = gameState.gameObjectState.derivedState.stackObjectStates(stackObjectId)
    for {
      target <- TargetChoice(stackObjectWithState, targetIdentifier)
      _ <- AddTarget(stackObjectId, target)
    } yield ()
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
