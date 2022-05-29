package mtg.instructions.joiners

import mtg.core.PlayerId
import mtg.effects.InstructionResolutionContext
import mtg.game.state.GameState
import mtg.instructions.grammar.VerbInflection
import mtg.instructions.{InstructionResult, IntransitiveInstructionVerb}
import mtg.utils.TextUtils._

object Then {
  def apply(verbs: IntransitiveInstructionVerb[PlayerId]*): IntransitiveInstructionVerb[PlayerId] = new IntransitiveInstructionVerb[PlayerId] {
    override def inflect(verbInflection: VerbInflection, cardName: String): String = {
      verbs.map(_.inflect(verbInflection, cardName)).toCommaList("then", true)
    }
    override def resolve(subject: PlayerId, gameState: GameState, resolutionContext: InstructionResolutionContext): InstructionResult = {
      (verbs.map(IntransitiveInstructionVerb.WithKnownSubject(subject, _)), resolutionContext)
    }
  }
}
