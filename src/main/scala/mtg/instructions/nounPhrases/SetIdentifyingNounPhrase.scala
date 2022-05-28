package mtg.instructions.nounPhrases

import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.instructions.TextComponent
import mtg.instructions.grammar.{GrammaticalNumber, GrammaticalPerson}

trait SetIdentifyingNounPhrase[+T] extends NounPhrase {
  def identifyAll(gameState: GameState, resolutionContext: StackObjectResolutionContext): (Seq[T], StackObjectResolutionContext)
  def getPossessiveText(cardName: String): String = getText(cardName) + "'s"
}
