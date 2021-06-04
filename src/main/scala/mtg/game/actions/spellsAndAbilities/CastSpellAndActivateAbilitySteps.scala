package mtg.game.actions.spellsAndAbilities

import mtg.game.ObjectId
import mtg.game.state.{BackupAction, GameAction, GameActionResult, GameState, InternalGameAction}

case class CastSpellAndActivateAbilitySteps(getFinalAction: ObjectId => GameAction, backupAction: BackupAction) extends InternalGameAction {
  override def execute(currentGameState: GameState): GameActionResult = {
    val stackObjectId = currentGameState.gameObjectState.stack.last.objectId
    Seq(
        ChooseTargets(stackObjectId, backupAction),
        PayCosts(stackObjectId, backupAction),
        getFinalAction(stackObjectId))
  }
}
