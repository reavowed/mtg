package mtg.stack.adding

import mtg.cards.text.{ModalEffectParagraph, SimpleSpellEffectParagraph}
import mtg.game.state._
import mtg.game.{ObjectId, PlayerId}

case class ChooseModes(stackObjectId: ObjectId) extends ExecutableGameAction[Unit] {
  override def execute()(implicit gameState: GameState): PartialGameActionResult[Unit] = {
    val stackObjectWithState = gameState.gameObjectState.derivedState.stackObjectStates(stackObjectId)
    stackObjectWithState.characteristics.rulesText.ofType[ModalEffectParagraph].headOption match {
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

case class ModeChoice(playerToAct: PlayerId, stackObjectId: ObjectId, modes: Seq[SimpleSpellEffectParagraph]) extends DirectChoice[Int] {
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

