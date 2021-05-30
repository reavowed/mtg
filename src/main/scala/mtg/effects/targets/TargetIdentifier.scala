package mtg.effects.targets

import mtg.effects.filters.Filter
import mtg.effects.identifiers.Identifier
import mtg.effects.oneshot.OneShotEffectResolutionContext
import mtg.game.ObjectOrPlayer
import mtg.game.state.GameState

trait TargetIdentifier extends Identifier[ObjectOrPlayer] {
  def filter: Filter[ObjectOrPlayer]
  override def getText(cardName: String): String = text
  def text: String = s"target ${filter.text}"

  override def get(gameState: GameState, resolutionContext: OneShotEffectResolutionContext): (ObjectOrPlayer, OneShotEffectResolutionContext) = {
    resolutionContext.popTarget
  }
  def getValidChoices(gameState: GameState): Seq[ObjectOrPlayer] = {
    gameState.gameObjectState.allObjects
      .map(_.objectId)
      .filter(filter.isValid(_, gameState))
      .toSeq
  }
}
