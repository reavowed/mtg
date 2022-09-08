package mtg.instructions.nouns

import mtg.definitions.ObjectId
import mtg.definitions.types.Type
import mtg.effects.EffectContext
import mtg.game.state.GameState

case class TypeNoun(t: Type) extends ClassNoun[ObjectId] {
  override def getSingular(cardName: String): String = t.name.toLowerCase
  override def getAll(gameState: GameState, effectContext: EffectContext): Seq[ObjectId] = {
    gameState.gameObjectState.derivedState.permanentStates.toSeq.filter(_._2.characteristics.types.contains(t)).map(_._1)
  }
}
