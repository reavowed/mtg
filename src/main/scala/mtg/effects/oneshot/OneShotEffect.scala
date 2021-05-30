package mtg.effects.oneshot

import mtg.SeqExtensionMethods
import mtg.effects.targets.TargetIdentifier
import mtg.game.state.GameState

abstract class OneShotEffect extends Product {
  def targetIdentifiers: Seq[TargetIdentifier] = productIterator.toSeq.ofType[TargetIdentifier]
  def getText(cardName: String): String
  def resolve(gameState: GameState, resolutionContext: OneShotEffectResolutionContext): OneShotEffectResult
}
