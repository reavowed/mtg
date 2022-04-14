package mtg.instructions.joiners

import mtg.core.PlayerId
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.instructions.{Instruction, InstructionChoice, InstructionResult, IntransitiveInstructionVerb, VerbInflection}

case class May(verb: IntransitiveInstructionVerb[PlayerId]) extends IntransitiveInstructionVerb[PlayerId] {
  override def inflect(verbInflection: VerbInflection, cardName: String): String = "may " + verb.inflect(verbInflection, cardName)
  override def resolve(subject: PlayerId, gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = ???
}

case class MayChoice(playerChoosing: PlayerId, instruction: Instruction, text: String) extends InstructionChoice {
  override def parseDecision(serializedDecision: String, resolutionContext: StackObjectResolutionContext)(implicit gameState: GameState): Option[InstructionResult] = {
    if (serializedDecision == "Yes")
      Some((instruction, resolutionContext))
    else if (serializedDecision == "No")
      Some(resolutionContext)
    else
      None
  }
}
