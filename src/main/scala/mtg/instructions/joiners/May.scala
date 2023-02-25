package mtg.instructions.joiners

import mtg.definitions.PlayerId
import mtg.effects.InstructionResolutionContext
import mtg.game.state.{Choice, GameAction, GameState}
import mtg.instructions.grammar.VerbInflection
import mtg.instructions.{InstructionAction, IntransitiveInstructionVerb}

case class May(verb: IntransitiveInstructionVerb[PlayerId]) extends IntransitiveInstructionVerb[PlayerId] {
  override def inflect(verbInflection: VerbInflection, cardName: String): String = "may " + verb.inflect(VerbInflection.Infinitive, cardName)
  override def resolve(subject: PlayerId): InstructionAction = InstructionAction { (resolutionContext, gameState) =>
    MayChoice(subject, verb.inflect(VerbInflection.Imperative, resolutionContext.cardName(gameState)))
      .flatMap(handleChoice(_, subject, resolutionContext))
  }
  private def handleChoice(wasChosen: Boolean, subject: PlayerId, resolutionContext: InstructionResolutionContext): GameAction[InstructionResolutionContext] = {
    if (wasChosen) {
      verb.resolve(subject)(resolutionContext)
    } else resolutionContext
  }
}

case class MayChoice(playerToAct: PlayerId, text: String) extends Choice[Boolean] {
  override def handleDecision(serializedDecision: String)(implicit gameState: GameState): Option[Boolean] = {
    if (serializedDecision == "Yes")
      Some(true)
    else if (serializedDecision == "No")
      Some(false)
    else
      None
  }
}
