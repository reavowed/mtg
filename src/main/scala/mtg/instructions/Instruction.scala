package mtg.instructions

import mtg.effects.StackObjectResolutionContext
import mtg.effects.targets.Target
import mtg.game.state.GameState
import mtg.instructions.verbs.Add

trait Instruction extends TextComponent {
  def targetIdentifiers: Seq[Target[_]] = {
    def helper(refs: Seq[Any], targets: Seq[Target[_]]): Seq[Target[_]] = {
      refs match {
        case (targetIdentifier: Target[_]) +: tail =>
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
