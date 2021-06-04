package mtg.effects.targets

import mtg.effects.{EffectContext, StackObjectResolutionContext}
import mtg.effects.filters.Filter
import mtg.effects.identifiers.Identifier
import mtg.game.{ObjectId, ObjectOrPlayer}
import mtg.game.state.GameState

sealed abstract class TargetIdentifier[T <: ObjectOrPlayer](filter: Filter[T]) extends Identifier[T] {
  def getText(cardName: String): String = s"target ${filter.getText(cardName)}"

  def get(gameState: GameState, resolutionContext: StackObjectResolutionContext): (T, StackObjectResolutionContext) = {
    resolutionContext.popTarget.mapLeft(_.asInstanceOf[T])
  }
  def getValidChoices(gameState: GameState, effectContext: EffectContext): Seq[ObjectOrPlayer]
}

abstract class ObjectOrPlayerTargetIdentifier(filter: Filter[ObjectOrPlayer]) extends TargetIdentifier[ObjectOrPlayer](filter) {
  def getValidChoices(gameState: GameState, effectContext: EffectContext): Seq[ObjectOrPlayer] = {
    (gameState.gameObjectState.allObjects.map(_.objectId) ++ gameState.gameData.playersInTurnOrder)
      .filter(filter.isValid(_, effectContext, gameState))
      .toSeq
  }
}

class ObjectTargetIdentifier(filter: Filter[ObjectId]) extends TargetIdentifier[ObjectId](filter) {
  def getValidChoices(gameState: GameState, effectContext: EffectContext): Seq[ObjectOrPlayer] = {
    gameState.gameObjectState.allObjects
      .map(_.objectId)
      .filter(filter.isValid(_, effectContext, gameState))
      .toSeq
  }
}
