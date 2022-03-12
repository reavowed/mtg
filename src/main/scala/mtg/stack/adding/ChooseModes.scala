package mtg.stack.adding

import mtg.cards.text.{ModalInstructionParagraph, SimpleInstructionParagraph}
import mtg.core.{ObjectId, PlayerId}
import mtg.game.state._

case class ChooseModes(stackObjectId: ObjectId) extends ExecutableGameAction[Unit] {
  override def execute()(implicit gameState: GameState): PartialGameActionResult[Unit] = {
    val stackObjectWithState = gameState.gameObjectState.derivedState.stackObjectStates(stackObjectId)
    stackObjectWithState.characteristics.rulesText.ofType[ModalInstructionParagraph].headOption match {
      case Some(modalEffectParagraph) =>
        PartialGameActionResult.ChildWithCallback(
          ModeChoice(stackObjectWithState.controller, stackObjectId, modalEffectParagraph.modes),
          setMode(stackObjectWithState))
      case None =>
        PartialGameActionResult.Value(())
    }
  }
  private def setMode(stackObjectWithState: StackObjectWithState)(modeIndex: Int, gameState: GameState): PartialGameActionResult[Unit] = {
    PartialGameActionResult.child(WrappedOldUpdates(SetMode(stackObjectWithState.gameObject.objectId, modeIndex)))
  }
}

case class ModeChoice(playerToAct: PlayerId, stackObjectId: ObjectId, modes: Seq[SimpleInstructionParagraph]) extends Choice[Int] {
  def handleDecision(serializedDecision: String)(implicit gameState: GameState): Option[Int] = {
    for {
      modeIndex <- serializedDecision.toIntOption
      if modeIndex < modes.length
    } yield modeIndex
  }
}

case class SetMode(stackObjectId: ObjectId, modeIndex: Int) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.updateStackObject(stackObjectId, _.addMode(modeIndex))
  }
  override def canBeReverted: Boolean = true
}

