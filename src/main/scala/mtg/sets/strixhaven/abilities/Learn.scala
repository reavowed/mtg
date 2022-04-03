package mtg.sets.strixhaven.abilities

import mtg.core.types.SpellType.Lesson
import mtg.core.{ObjectId, PlayerId}
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.{CurrentCharacteristics, GameState}
import mtg.instructions.{Instruction, InstructionChoice, InstructionResult}

object Learn extends Instruction {
  override def getText(cardName: String): String = "learn"
  override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    val player = resolutionContext.controllingPlayer
    LearnChoice(
      player,
      gameState.gameObjectState.sideboards(player)
        .map(_.objectId)
        .filter(id => CurrentCharacteristics.getCharacteristics(id, gameState).subtypes.contains(Lesson)))
  }
}

case class LearnChoice(playerChoosing: PlayerId, possibleLessons: Seq[ObjectId]) extends InstructionChoice {
  override def parseDecision(serializedDecision: String, resolutionContext: StackObjectResolutionContext): Option[InstructionResult] = {
    None
  }
}
