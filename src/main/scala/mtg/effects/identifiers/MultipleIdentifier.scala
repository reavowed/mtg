package mtg.effects.identifiers

import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.instructions.TextComponent
import mtg.text.NounPhrase

trait MultipleIdentifier[+T] extends TextComponent {
  def getAll(gameState: GameState, resolutionContext: StackObjectResolutionContext): (Seq[T], StackObjectResolutionContext)
  def getNounPhrase(cardName: String): NounPhrase
  override def getText(cardName: String): String = getNounPhrase(cardName).text
}
