package mtg.effects

import mtg._
import mtg.effects.oneshot.{OneShotEffectResolutionContext, OneShotEffectResult}
import mtg.effects.targets.TargetIdentifier
import mtg.game.ObjectId
import mtg.game.state.GameState

sealed class Effect

abstract class OneShotEffect extends Effect with Product {
  def targetIdentifiers: Seq[TargetIdentifier[_]] = productIterator.toSeq.ofType[TargetIdentifier[_]]
  def getText(cardName: String): String
  def resolve(gameState: GameState, resolutionContext: OneShotEffectResolutionContext): OneShotEffectResult
}

abstract class ContinuousEffect extends Effect {
  def affectedObject: ObjectId
}
