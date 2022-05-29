package mtg.instructions.joiners

import mtg.core.PlayerId
import mtg.effects.InstructionResolutionContext
import mtg.game.state.GameState
import mtg.instructions.grammar.VerbInflection
import mtg.instructions.{InstructionChoice, InstructionResult, IntransitiveInstructionVerb}

case class May(verb: IntransitiveInstructionVerb[PlayerId]) extends IntransitiveInstructionVerb[PlayerId] {
  override def inflect(verbInflection: VerbInflection, cardName: String): String = "may " + verb.inflect(VerbInflection.Infinitive, cardName)
  override def resolve(subject: PlayerId, gameState: GameState, resolutionContext: InstructionResolutionContext): InstructionResult = {
    MayChoice(subject, verb, verb.inflect(VerbInflection.Imperative, resolutionContext.cardName(gameState)))
  }
}

case class MayChoice(playerChoosing: PlayerId, verb: IntransitiveInstructionVerb[PlayerId], text: String) extends InstructionChoice {
  override def parseDecision(serializedDecision: String, resolutionContext: InstructionResolutionContext)(implicit gameState: GameState): Option[InstructionResult] = {
    if (serializedDecision == "Yes")
      Some(verb.resolve(playerChoosing, gameState, resolutionContext))
    else if (serializedDecision == "No")
      Some(resolutionContext)
    else
      None
  }
}
