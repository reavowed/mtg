package mtg.sets.strixhaven.abilities

import mtg.core.PlayerId
import mtg.core.types.SpellType.Lesson
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.{GameState, InternalGameAction}
import mtg.instructions.{Instruction, InstructionChoice, InstructionResult}

object Learn extends Instruction {
  override def getText(cardName: String): String = "learn"
  override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    val player = resolutionContext.controllingPlayer
    LearnChoice(
      player,
      gameState.gameObjectState.sideboards(player).filter(_.cardDefinition.subtypes.contains(Lesson)).map(_.id))
  }
}

case class LearnChoice(playerChoosing: PlayerId, possibleLessons: Seq[String]) extends InstructionChoice {
  override def parseDecision(serializedDecision: String): Option[(Option[InternalGameAction], StackObjectResolutionContext)] = {
    ???
  }
}
