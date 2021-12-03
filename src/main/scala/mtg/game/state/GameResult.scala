package mtg.game.state

sealed trait GameResult

object GameResult {
  object Tie extends GameResult
}
