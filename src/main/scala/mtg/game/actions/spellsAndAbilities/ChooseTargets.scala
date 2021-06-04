package mtg.game.actions.spellsAndAbilities

import mtg.effects.EffectCreationContext
import mtg.effects.continuous.TargetPreventionEffect
import mtg.effects.targets.TargetIdentifier
import mtg.events.targets.AddTarget
import mtg.game.state._
import mtg.game.{ObjectId, ObjectOrPlayer, PlayerId}

case class ChooseTargets(objectId: ObjectId, backupAction: BackupAction) extends InternalGameAction {
  override def execute(currentGameState: GameState): GameActionResult = {
    currentGameState.gameObjectState.derivedState.spellStates.get(objectId).toSeq.flatMap { stackObjectWithState =>
      val targetIdentifiers = TargetIdentifier.getAll(stackObjectWithState)
      targetIdentifiers.map(targetIdentifier => TargetChoice(
        stackObjectWithState.controller,
        objectId,
        targetIdentifier.getText(stackObjectWithState.characteristics.name),
        targetIdentifier.getValidChoices(stackObjectWithState, currentGameState, EffectCreationContext(stackObjectWithState.controller))))
    }
  }
}

case class ChosenTarget(objectOrPlayer: ObjectOrPlayer)
case class TargetChoice(playerToAct: PlayerId, objectId: ObjectId, targetDescription: String, validOptions: Seq[ObjectOrPlayer]) extends TypedPlayerChoice[ChosenTarget] {
  override def parseOption(serializedChosenOption: String, currentGameState: GameState): Option[ChosenTarget] = {
    validOptions.find(_.toString == serializedChosenOption).map(ChosenTarget)
  }
  override def handleDecision(chosenOption: ChosenTarget, currentGameState: GameState): GameActionResult = {
    AddTarget(objectId, chosenOption.objectOrPlayer)
  }
}
