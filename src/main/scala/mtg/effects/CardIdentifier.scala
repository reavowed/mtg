package mtg.effects

import mtg.game.objects.ObjectId
import mtg.game.state.GameState

trait CardIdentifier {
  def getCard(gameState: GameState, resolutionContext: ResolutionContext): ObjectId
  def text: String
}

object CardIdentifier {
  object It extends CardIdentifier {
    override def getCard(gameState: GameState, resolutionContext: ResolutionContext): ObjectId = {
      resolutionContext.identifiedObjects.last
    }

    override def text: String = "it"
  }
}
