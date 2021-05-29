package mtg.effects.filters

import mtg.effects.ResolutionContext
import mtg.game.ObjectId
import mtg.game.state.{Characteristics, GameState}

case class CompoundCharacteristicFilter(characteristicFilters: CharacteristicFilter*) extends CharacteristicFilter {
  def isValid(characteristics: Characteristics, gameState: GameState): Boolean = {
    characteristicFilters.forall(_.isValid(characteristics, gameState))
  }
  override def text: String = characteristicFilters.map(_.text).mkString(" ")
}
