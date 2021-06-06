package mtg.game.stack.steps

import mtg.game.ObjectId
import mtg.game.state._

case class CastSpellAndActivateAbilitySteps(getFinalAction: ObjectId => GameAction, backupAction: BackupAction) extends InternalGameAction {
  override def execute(currentGameState: GameState): GameActionResult = {
    val stackObjectId = currentGameState.gameObjectState.stack.last.objectId
    Seq(
      ChooseTargets(stackObjectId, backupAction),
      PayCosts(stackObjectId, backupAction),
      getFinalAction(stackObjectId))
  }
}
