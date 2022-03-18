package mtg.instructions.nouns

import mtg.core.ObjectId
import mtg.effects.EffectContext
import mtg.game.state.GameState
import mtg.utils.CaseObjectWithName

trait Noun {
  def singular: String
  def plural: String
  
  def describes(objectId: ObjectId, gameState: GameState, effectContext: EffectContext): Boolean
}

object Noun {
  trait RegularCaseObject extends Noun with CaseObjectWithName {
    override def singular: String = name
    override def plural: String = name + "s"
  }
}
