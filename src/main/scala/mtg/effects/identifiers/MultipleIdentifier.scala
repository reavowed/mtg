package mtg.effects.identifiers

import mtg.core.ObjectOrPlayerId
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.text.NounPhrase

trait MultipleIdentifier[+T <: ObjectOrPlayerId] {
  def getAll(gameState: GameState, resolutionContext: StackObjectResolutionContext): (Seq[T], StackObjectResolutionContext)
  def getNounPhrase(cardName: String): NounPhrase
  def getText(cardName: String): String = getNounPhrase(cardName).text
}
