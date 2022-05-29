package mtg.instructions.nounPhrases

import mtg.effects.InstructionResolutionContext
import mtg.game.state.GameState
import mtg.instructions.TextComponent
import mtg.instructions.grammar.{GrammaticalNumber, GrammaticalPerson}

trait SetIdentifyingNounPhrase[+T] extends NounPhrase {
  def identifyAll(gameState: GameState, resolutionContext: InstructionResolutionContext): (Seq[T], InstructionResolutionContext)
  def getPossessiveText(cardName: String): String = getText(cardName) + "'s"
}
