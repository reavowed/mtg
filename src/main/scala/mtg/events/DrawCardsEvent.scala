package mtg.events

import mtg.game.{GameState, PlayerIdentifier}

case class DrawCardsEvent(playerIdentifier: PlayerIdentifier, numberOfCards: Int) extends Event {
  override def execute(currentGameState: GameState): Either[GameState, Seq[Event]] = {
    Right(Seq.fill(numberOfCards)(DrawCardEvent(playerIdentifier)))
  }
}
