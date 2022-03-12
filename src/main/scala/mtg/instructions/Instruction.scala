package mtg.instructions

import mtg._
import mtg.effects.StackObjectResolutionContext
import mtg.effects.targets.TargetIdentifier
import mtg.game.state.GameState

trait Instruction extends Product {
  def targetIdentifiers: Seq[TargetIdentifier[_]] = productIterator.toSeq.ofType[TargetIdentifier[_]]

  def getText(cardName: String): String

  def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult
}
