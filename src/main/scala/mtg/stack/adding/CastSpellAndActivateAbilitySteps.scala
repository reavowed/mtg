package mtg.stack.adding

import mtg.game.ObjectId
import mtg.game.state._

case class CastSpellAndActivateAbilitySteps(getFinalAction: ObjectId => GameAction, backupAction: BackupAction) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    val stackObjectId = gameState.gameObjectState.stack.last.objectId
    Seq(
      ChooseTargets(stackObjectId, backupAction),
      PayCosts(stackObjectId, backupAction),
      getFinalAction(stackObjectId))
  }

  override def canBeReverted: Boolean = true
}
