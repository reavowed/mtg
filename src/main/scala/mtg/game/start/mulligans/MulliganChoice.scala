package mtg.game.start.mulligans

import mtg.game.PlayerId
import mtg.game.state.{Choice, Decision, GameActionResult, GameState, InternalGameAction}
import mtg.game.state.history.LogEvent

case class MulliganChoice(playerToAct: PlayerId, mulligansSoFar: Int) extends Choice.WithParser
{
  override def parser: PartialFunction[String, Decision] = {
    case "M" => MulliganDecision.Mulligan(playerToAct, mulligansSoFar)
    case "K" => MulliganDecision.Keep(playerToAct, mulligansSoFar)
  }
}

trait MulliganDecision extends InternalGameAction {
  def player: PlayerId
  override def canBeReverted: Boolean = false
}
object MulliganDecision {
  case class Mulligan(player: PlayerId, mulligansAlready: Int) extends MulliganDecision {
    override def execute(gameState: GameState): GameActionResult = {
       LogEvent.Mulligan(player, gameState.gameData.startingHandSize - mulligansAlready)
    }
  }
  case class Keep(player: PlayerId, mulligansAlready: Int) extends MulliganDecision {
    override def execute(gameState: GameState): GameActionResult = {
      val action = if (mulligansAlready > 0)
        Seq(ReturnCardsToLibraryChoice(player, mulligansAlready, gameState))
      else
        Nil
      (action, LogEvent.KeepHand(player, gameState.gameData.startingHandSize - mulligansAlready))
    }
    override def canBeReverted: Boolean = false
  }
}
