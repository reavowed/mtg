package mtg.effects

import mtg._
import mtg.continuousEffects.PreventionEffect
import mtg.effects.oneshot.OneShotEffectResult
import mtg.effects.targets.TargetIdentifier
import mtg.game.state.GameState

trait OneShotEffect extends Product {
  def targetIdentifiers: Seq[TargetIdentifier[_]] = productIterator.toSeq.ofType[TargetIdentifier[_]]
  def getText(cardName: String): String
  def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): OneShotEffectResult
}
