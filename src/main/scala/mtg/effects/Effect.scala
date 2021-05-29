package mtg.effects

import mtg.SeqExtensionMethods
import mtg.effects.targets.TargetIdentifier
import mtg.game.ObjectOrPlayer
import mtg.game.state.GameState

abstract class Effect extends Product {
  def targetIdentifiers: Seq[TargetIdentifier] = productIterator.toSeq.ofType[TargetIdentifier]
  def getText(cardName: String): String
  def resolve(gameState: GameState, resolutionContext: ResolutionContext): EffectResult
}
