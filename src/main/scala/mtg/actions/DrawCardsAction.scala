package mtg.actions

import mtg.definitions.PlayerId
import mtg.game.state.{DelegatingGameObjectAction, GameObjectAction, GameState}

case class DrawCardsAction(playerIdentifier: PlayerId, numberOfCards: Int) extends DelegatingGameObjectAction {
  override def delegate(implicit gameState: GameState): Seq[GameObjectAction[_]] = {
    Seq.fill(numberOfCards)(DrawCardAction(playerIdentifier))
  }
}
