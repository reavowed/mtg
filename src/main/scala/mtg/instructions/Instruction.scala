package mtg.instructions

import mtg.core.zones.ZoneType
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.instructions.nounPhrases.Target
import mtg.instructions.verbs.Add

trait ResolvableInstructionPart {
  def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult
}

trait Instruction extends ResolvableInstructionPart with TextComponent {
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
  def functionalZones: Option[Set[ZoneType]] = None
}
