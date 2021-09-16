package mtg.stack.adding

import mtg.cards.text.{ModalEffectParagraph, SimpleSpellEffectParagraph}
import mtg.game.state._
import mtg.game.{ObjectId, PlayerId}

case class ChooseModes(objectId: ObjectId, backupAction: BackupAction) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.derivedState.spellStates.get(objectId).toSeq.flatMap { stackObjectWithState =>
      stackObjectWithState.characteristics.rulesText.ofType[ModalEffectParagraph].map(modalParagraph =>
        ModeChoice(
          stackObjectWithState.controller,
          stackObjectWithState,
          modalParagraph.modes
        )
      )
    }
  }
  override def canBeReverted: Boolean = true
}

case class ModeChoice(playerToAct: PlayerId, objectWithState: StackObjectWithState, modes: Seq[SimpleSpellEffectParagraph]) extends Choice {
  override def parseDecision(serializedChosenOption: String): Option[Decision] = ???
}
