package mtg.actions

import mtg.game.state.{ExecutableGameAction, GameResult, GameState, PartialGameActionResult}

case class GameResultAction(gameResult: GameResult) extends ExecutableGameAction[Nothing] {
  override def execute()(implicit gameState: GameState): PartialGameActionResult[Nothing] = PartialGameActionResult.GameOver(gameResult)
}
