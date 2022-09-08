package mtg.stack.adding

import mtg.actions.stack.SetMode
import mtg.cards.text.{ModalInstructionParagraph, SimpleInstructionParagraph}
import mtg.definitions.{ObjectId, PlayerId}
import mtg.game.state._

case class ChooseModes(stackObjectId: ObjectId) extends DelegatingGameAction[Unit] {
  override def delegate(implicit gameState: GameState): GameAction[Unit] = {
    val stackObjectWithState = gameState.gameObjectState.derivedState.stackObjectStates(stackObjectId)
    stackObjectWithState.characteristics.instructionParagraphs.ofType[ModalInstructionParagraph].headOption match {
      case Some(modalEffectParagraph) =>
        for {
          modeIndex <- ModeChoice(stackObjectWithState.controller, stackObjectId, modalEffectParagraph.modes)
          _ <- SetMode(stackObjectId, modeIndex)
        } yield ()
      case None =>
        ()
    }
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
