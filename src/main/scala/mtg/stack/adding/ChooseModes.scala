package mtg.stack.adding

import mtg.cards.text.{ModalEffectParagraph, SimpleSpellEffectParagraph}
import mtg.game.state._
import mtg.game.{ObjectId, PlayerId}

case class ChooseModes(stackObjectId: ObjectId, backupAction: BackupAction) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.derivedState.stackObjectStates.get(stackObjectId).toSeq.flatMap { stackObjectWithState =>
      stackObjectWithState.characteristics.rulesText.ofType[ModalEffectParagraph].map(modalParagraph =>
        ModeChoice(
          stackObjectWithState.controller,
          stackObjectId,
          modalParagraph.modes
        )
      )
    }
  }
  override def canBeReverted: Boolean = true
}

case class ModeChoice(playerToAct: PlayerId, stackObjectId: ObjectId, modes: Seq[SimpleSpellEffectParagraph]) extends Choice {
  override def parseDecision(serializedChosenOption: String): Option[Decision] = {
    for {
      modeIndex <- serializedChosenOption.toIntOption
      if modeIndex < modes.length
    } yield ChooseMode(stackObjectId, modeIndex)
  }
}

case class ChooseMode(stackObjectId: ObjectId, modeIndex: Int) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.updateStackObject(stackObjectId, _.addMode(modeIndex))
  }
  override def canBeReverted: Boolean = true
}

