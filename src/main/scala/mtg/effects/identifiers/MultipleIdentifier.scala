package mtg.effects.identifiers

import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.text.NounPhrase

trait MultipleIdentifier[+T] {
  def getAll(gameState: GameState, resolutionContext: StackObjectResolutionContext): (Seq[T], StackObjectResolutionContext)
  def getNounPhrase(cardName: String): NounPhrase
  def getText(cardName: String): String = getNounPhrase(cardName).text
}
