package mtg.effects

import mtg._
import mtg.effects.continuous.PreventionEffect
import mtg.effects.oneshot.OneShotEffectResult
import mtg.effects.targets.TargetIdentifier
import mtg.game.state.GameState

trait Effect

trait OneShotEffect extends Effect with Product {
  def targetIdentifiers: Seq[TargetIdentifier[_]] = productIterator.toSeq.ofType[TargetIdentifier[_]]
  def getText(cardName: String): String
  def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): OneShotEffectResult
}

trait ContinuousEffect extends Effect
object ContinuousEffect {
  def fromRules = PreventionEffect.fromRules
}
