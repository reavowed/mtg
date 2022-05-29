package mtg.instructions.verbs

import mtg.actions.AddManaAction
import mtg.core.PlayerId
import mtg.core.symbols.ManaSymbol
import mtg.effects.InstructionResolutionContext
import mtg.game.state.GameState
import mtg.instructions.{InstructionResult, IntransitiveInstructionVerb, Verb}

case class Add(symbols: ManaSymbol*) extends Verb.WithSuffix(Verb.Add, symbols.map(_.text).mkString) with IntransitiveInstructionVerb[PlayerId] {
  override def resolve(playerId: PlayerId, gameState: GameState, resolutionContext: InstructionResolutionContext): InstructionResult = {
    (AddManaAction(playerId, symbols), resolutionContext)
  }
}
