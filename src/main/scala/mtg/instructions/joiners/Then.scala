package mtg.instructions.joiners

import mtg.core.PlayerId
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.instructions.{Instruction, InstructionResult, IntransitiveInstructionVerb, VerbInflection}
import mtg.utils.TextUtils._

object Then {
  def apply(verbs: IntransitiveInstructionVerb[PlayerId]*): IntransitiveInstructionVerb[PlayerId] = new IntransitiveInstructionVerb[PlayerId] {
    override def inflect(verbInflection: VerbInflection, cardName: String): String = {
      verbs.map(_.inflect(verbInflection, cardName)).toCommaList("then", true)
    }
    override def resolve(subject: PlayerId, gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
      (verbs.map(IntransitiveInstructionVerb.WithKnownSubject(subject, _)), resolutionContext)
    }
  }
}