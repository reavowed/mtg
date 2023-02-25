package mtg.instructions.joiners

import mtg.definitions.PlayerId
import mtg.instructions.grammar.VerbInflection
import mtg.instructions.{InstructionAction, IntransitiveInstructionVerb}
import mtg.stack.resolving.ResolveInstructions
import mtg.utils.TextUtils._

object Then {
  def apply(verbs: IntransitiveInstructionVerb[PlayerId]*): IntransitiveInstructionVerb[PlayerId] = new IntransitiveInstructionVerb[PlayerId] {
    override def inflect(verbInflection: VerbInflection, cardName: String): String = {
      verbs.map(_.inflect(verbInflection, cardName)).toCommaList("then", true)
    }
    override def resolve(subject: PlayerId): InstructionAction = { resolutionContext =>
      ResolveInstructions.executeInstructions(verbs.map(IntransitiveInstructionVerb.WithKnownSubject(subject, _)), resolutionContext)
    }
  }
}
