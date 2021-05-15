package mtg.game.start.mulligans

import mtg.game.PlayerIdentifier
import mtg.game.state.{GameActionManager, GameAction, GameState}

case class DrawAndMulliganAction(playersToDrawAndMulligan: Seq[PlayerIdentifier], mulligansSoFar: Int) extends GameActionManager {
  override def execute(currentGameState: GameState): Seq[GameAction] = {
    val drawActions = Seq(DrawStartingHandsEvent(playersToDrawAndMulligan))
    val mulliganChoices = playersToDrawAndMulligan.map(MulliganChoice(_, mulligansSoFar))
    val executeMulligansAction = ExecuteMulligansAction(mulligansSoFar)
    drawActions ++ mulliganChoices :+ executeMulligansAction
  }
}
