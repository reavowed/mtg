package mtg.instructions

import mtg.effects.StackObjectResolutionContext
import mtg.effects.targets.TargetIdentifier
import mtg.game.state.GameState
import mtg.instructions.verbs.Add

trait Instruction extends TextComponent {
  def targetIdentifiers: Seq[TargetIdentifier[_]] = {
    def helper(refs: Seq[Any], targets: Seq[TargetIdentifier[_]]): Seq[TargetIdentifier[_]] = {
      refs match {
        case (targetIdentifier: TargetIdentifier[_]) +: tail =>
          helper(tail, targets :+ targetIdentifier)
        case (product: Product) +: tail =>
          helper(product.productIterator.toSeq ++ tail, targets)
        case _ +: tail =>
          helper(tail, targets)
        case Nil =>
          targets
      }
    }
    helper(Seq(this), Nil)
  }
  def couldAddMana: Boolean = {
    def helper(refs: Seq[Any]): Boolean = {
      refs match {
        case (_: Add) +: tail =>
          true
        case (product: Product) +: tail =>
          helper(product.productIterator.toSeq ++ tail)
        case _ +: tail =>
          helper(tail)
        case Nil =>
          false
      }
    }
    helper(Seq(this))
  }

  def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult
}
