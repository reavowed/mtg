package mtg.actions

import mtg.core.PlayerId
import mtg.game.state.{DelegatingGameObjectAction, GameObjectAction, GameState}

case class DrawCardsAction(playerIdentifier: PlayerId, numberOfCards: Int) extends DelegatingGameObjectAction {
  override def delegate(implicit gameState: GameState): Seq[GameObjectAction] = {
    Seq.fill(numberOfCards)(DrawCardAction(playerIdentifier))
  }
}
