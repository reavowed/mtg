package mtg.game.loop

import mtg.game.{GameState, PlayerIdentifier}

abstract class Choice {
  def gameState: GameState
  def playerToAct: PlayerIdentifier
  def handleAction(serializedAction: String): Either[Action, Either[Choice, GameResult]]
}

object Choice {
  implicit def toEither(choice: Choice): Either[Action, Either[Choice, GameResult]] = Right(Left(choice))
  implicit def toEither(f: GameState => Choice): GameState => Either[Action, Either[Choice, GameResult]] = gameState => f(gameState)
}
