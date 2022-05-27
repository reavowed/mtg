package mtg.instructions.nouns

import mtg.core.ObjectId
import mtg.effects.EffectContext
import mtg.game.state.GameState
import mtg.instructions.SuffixDescriptor
import mtg.instructions.adjectives.Adjective

/**
 * A noun that refers to a class of things that exist within the game
 */
trait ClassNoun[+T] extends Noun {
  def getAll(gameState: GameState, effectContext: EffectContext): Seq[T]
}

object ClassNoun {
  case class WithAdjective(adjective: Adjective, noun: ClassNoun[ObjectId]) extends ClassNoun[ObjectId] {
    override def getSingular(cardName: String): String = adjective.getText(cardName) + " " + noun.getSingular(cardName)
    override def getPlural(cardName: String): String = adjective.getText(cardName) + " " + noun.getPlural(cardName)
    override def getAll(gameState: GameState, effectContext: EffectContext): Seq[ObjectId] = {
      noun.getAll(gameState, effectContext).filter(adjective.describes(_, gameState, effectContext))
    }
  }
  case class WithSuffix(noun: ClassNoun[ObjectId], suffixDescriptor: SuffixDescriptor) extends ClassNoun[ObjectId] {
    override def getSingular(cardName: String): String = noun.getSingular(cardName) + " " + suffixDescriptor.getText(cardName)
    override def getPlural(cardName: String): String = noun.getPlural(cardName) + " " + suffixDescriptor.getText(cardName)
    override def getAll(gameState: GameState, effectContext: EffectContext): Seq[ObjectId] = {
      noun.getAll(gameState, effectContext).filter(suffixDescriptor.describes(_, gameState, effectContext))
    }
  }
}
