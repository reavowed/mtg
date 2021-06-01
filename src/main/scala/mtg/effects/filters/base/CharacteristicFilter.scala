package mtg.effects.filters.base

import mtg.effects.filters.Filter
import mtg.game.ObjectId
import mtg.game.state.{Characteristics, GameState}

trait CharacteristicFilter extends Filter[ObjectId] {
  def isValid(characteristics: Characteristics, gameState: GameState): Boolean

  override def isValid(objectId: ObjectId, gameState: GameState): Boolean = {
    objectId.findCurrentCharacteristics(gameState).exists(isValid(_, gameState))
  }

  def text: String
}
