package mtg.instructions.nounPhrases

import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.instructions.{TextComponent, VerbNumber, VerbPerson}

trait SetIdentifyingNounPhrase[+T] extends TextComponent {
  def identifyAll(gameState: GameState, resolutionContext: StackObjectResolutionContext): (Seq[T], StackObjectResolutionContext)
  def getPossessiveText(cardName: String): String = getText(cardName) + "'s"
  def person: VerbPerson
  def number: VerbNumber
}
