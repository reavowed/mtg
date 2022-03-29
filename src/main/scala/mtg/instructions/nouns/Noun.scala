package mtg.instructions.nouns

import mtg.core.types.Type
import mtg.core.{ObjectId, ObjectOrPlayerId}
import mtg.effects.EffectContext
import mtg.game.state.GameState
import mtg.instructions.SuffixDescriptor
import mtg.instructions.adjectives.Adjective
import mtg.utils.CaseObjectWithName

trait Noun[-T <: ObjectOrPlayerId] {
  def getSingular(cardName: String): String
  def getPlural(cardName: String): String = getSingular(cardName) + "s"
  def describes(t: T, gameState: GameState, effectContext: EffectContext): Boolean
}

object Noun {
  trait RegularCaseObject[-T <: ObjectOrPlayerId] extends Noun[T] with CaseObjectWithName {
    override def getSingular(cardName: String): String = name.toLowerCase
  }
  case class TypeNoun(t: Type) extends Noun[ObjectId] {
    override def getSingular(cardName: String): String = t.name.toLowerCase
    override def describes(objectId: ObjectId, gameState: GameState, effectContext: EffectContext): Boolean = {
      gameState.gameObjectState.derivedState.permanentStates.get(objectId).exists(_.characteristics.types.contains(t))
    }
  }
  case class WithAdjective(adjective: Adjective, noun: Noun[ObjectId]) extends Noun[ObjectId] {
    override def getSingular(cardName: String): String = adjective.getText(cardName) + " " + noun.getSingular(cardName)
    override def getPlural(cardName: String): String = adjective.getText(cardName) + " " + noun.getPlural(cardName)

    override def describes(objectId: ObjectId, gameState: GameState, effectContext: EffectContext): Boolean = {
      adjective.describes(objectId, gameState, effectContext) && noun.describes(objectId, gameState, effectContext)
    }
  }
  case class WithSuffix(noun: Noun[ObjectId], suffixDescriptor: SuffixDescriptor) extends Noun[ObjectId] {
    override def getSingular(cardName: String): String = noun.getSingular(cardName) + " " + suffixDescriptor.getText(cardName)
    override def getPlural(cardName: String): String = noun.getPlural(cardName) + " " +suffixDescriptor.getText(cardName)

    override def describes(objectId: ObjectId, gameState: GameState, effectContext: EffectContext): Boolean = {
      noun.describes(objectId, gameState, effectContext) && suffixDescriptor.describes(objectId, gameState, effectContext)
    }
  }
}
