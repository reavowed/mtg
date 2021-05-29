package mtg.effects.filters

import mtg.game.ObjectId
import mtg.game.state.{Characteristics, GameState}

trait CharacteristicFilter extends Filter[ObjectId] {
  def isValid(characteristics: Characteristics, gameState: GameState): Boolean
  override def isValid(objectId: ObjectId, gameState: GameState): Boolean = {
    isValid(objectId.currentCharacteristics(gameState), gameState)
  }
}
