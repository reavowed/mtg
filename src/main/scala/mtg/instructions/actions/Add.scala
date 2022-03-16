package mtg.instructions.actions

import mtg.actions.AddManaAction
import mtg.core.PlayerId
import mtg.core.symbols.ManaSymbol
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.instructions.{InstructionResult, IntransitiveInstructionVerb}
import mtg.text.Verb

case class Add(symbols: ManaSymbol*) extends Verb.WithSuffix(Verb.Add, symbols.map(_.text).mkString) with IntransitiveInstructionVerb {
  override def resolve(playerId: PlayerId, gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    (AddManaAction(playerId, symbols), resolutionContext)
  }
}
