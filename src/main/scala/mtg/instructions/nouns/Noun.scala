package mtg.instructions.nouns

import mtg.core.ObjectId
import mtg.core.types.Type
import mtg.effects.EffectContext
import mtg.game.state.GameState
import mtg.instructions.adjectives.Adjective
import mtg.utils.CaseObjectWithName

trait Noun {
  def getSingular(cardName: String): String
  def getPlural(cardName: String): String
  
  def describes(objectId: ObjectId, gameState: GameState, effectContext: EffectContext): Boolean
}

object Noun {
  trait RegularCaseObject extends Noun with CaseObjectWithName {
    override def getSingular(cardName: String): String = name
    override def getPlural(cardName: String): String = name + "s"
  }
  case class WithAdjective(adjective: Adjective, noun: Noun) extends Noun {
    override def getSingular(cardName: String): String = adjective.getText(cardName) + " " + noun.getSingular(cardName)
    override def getPlural(cardName: String): String = adjective.getText(cardName) + " " + noun.getPlural(cardName)

    override def describes(objectId: ObjectId, gameState: GameState, effectContext: EffectContext): Boolean = {
      adjective.describes(objectId, gameState, effectContext) && noun.describes(objectId, gameState, effectContext)
    }
  }
}
