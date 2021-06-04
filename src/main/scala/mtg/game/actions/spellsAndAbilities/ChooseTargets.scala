package mtg.game.actions.spellsAndAbilities

import mtg.abilities.SpellAbility
import mtg.effects.continuous.TargetPreventionEffect
import mtg.effects.targets.TargetIdentifier
import mtg.events.targets.AddTarget
import mtg.game.state._
import mtg.game.{ObjectId, ObjectOrPlayer, PlayerId}

case class ChooseTargets(objectId: ObjectId, backupAction: BackupAction) extends InternalGameAction {
  override def execute(currentGameState: GameState): GameActionResult = {
    currentGameState.gameObjectState.derivedState.spellStates.get(objectId).toSeq.flatMap { stackObjectWithState =>
      val targetIdentifiers = stackObjectWithState.characteristics.abilities.ofType[SpellAbility].flatMap(_.effects).flatMap(_.targetIdentifiers)
      val targetPreventionEffects = currentGameState.gameObjectState.activeContinuousEffects.ofType[TargetPreventionEffect].toSeq
      targetIdentifiers.map(targetIdentifier => TargetChoice(
        stackObjectWithState.controller,
        objectId,
        targetIdentifier.getText(stackObjectWithState.characteristics.name),
        getValidTargets(targetIdentifier, currentGameState, stackObjectWithState, targetPreventionEffects)))
    }
  }

  def getValidTargets(targetIdentifier: TargetIdentifier[_], gameState: GameState, source: StackObjectWithState, targetPreventionEffects: Seq[TargetPreventionEffect]): Seq[ObjectOrPlayer] = {
    targetIdentifier.getValidChoices(gameState)
      .filter(choice => !targetPreventionEffects.exists(_.preventsTarget(source, choice, gameState)))
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
