package mtg.instructions.nouns

import mtg.core.types.Type
import mtg.core.{ObjectId, ObjectOrPlayerId}
import mtg.effects.EffectContext
import mtg.game.state.GameState
import mtg.instructions.SuffixDescriptor
import mtg.instructions.adjectives.Adjective
import mtg.utils.CaseObjectWithName

trait Noun[+T] {
  def getSingular(cardName: String): String
  def getPlural(cardName: String): String = getSingular(cardName) + "s"
  def getAll(gameState: GameState, effectContext: EffectContext): Seq[T]
}

object Noun {
  trait RegularCaseObject[+T] extends Noun[T] with CaseObjectWithName {
    override def getSingular(cardName: String): String = name.toLowerCase
  }
  case class TypeNoun(t: Type) extends Noun[ObjectId] {
    override def getSingular(cardName: String): String = t.name.toLowerCase
    override def getAll(gameState: GameState, effectContext: EffectContext): Seq[ObjectId] = {
      gameState.gameObjectState.derivedState.permanentStates.toSeq.filter(_._2.characteristics.types.contains(t)).map(_._1)
    }
  }
  case class WithAdjective(adjective: Adjective, noun: Noun[ObjectId]) extends Noun[ObjectId] {
    override def getSingular(cardName: String): String = adjective.getText(cardName) + " " + noun.getSingular(cardName)
    override def getPlural(cardName: String): String = adjective.getText(cardName) + " " + noun.getPlural(cardName)
    override def getAll(gameState: GameState, effectContext: EffectContext): Seq[ObjectId] = {
      noun.getAll(gameState, effectContext).filter(adjective.describes(_, gameState, effectContext))
    }
  }
  case class WithSuffix(noun: Noun[ObjectId], suffixDescriptor: SuffixDescriptor) extends Noun[ObjectId] {
    override def getSingular(cardName: String): String = noun.getSingular(cardName) + " " + suffixDescriptor.getText(cardName)
    override def getPlural(cardName: String): String = noun.getPlural(cardName) + " " + suffixDescriptor.getText(cardName)
    override def getAll(gameState: GameState, effectContext: EffectContext): Seq[ObjectId] = {
      noun.getAll(gameState, effectContext).filter(suffixDescriptor.describes(_, gameState, effectContext))
    }
  }
}
