package mtg.effects.identifiers

import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.instructions.TextComponent
import mtg.text.{VerbNumber, VerbPerson}

trait MultipleIdentifier[+T] extends TextComponent {
  def getAll(gameState: GameState, resolutionContext: StackObjectResolutionContext): (Seq[T], StackObjectResolutionContext)
  def getPossessiveText(cardName: String): String = getText(cardName) + "'s"
  def person: VerbPerson
  def number: VerbNumber
}
