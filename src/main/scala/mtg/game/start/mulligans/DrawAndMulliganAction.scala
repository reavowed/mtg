package mtg.game.start.mulligans

import mtg.game.PlayerIdentifier
import mtg.game.state.{AutomaticGameAction, GameAction, GameState}

case class DrawAndMulliganAction(playersToDrawAndMulligan: Seq[PlayerIdentifier], mulligansSoFar: Int) extends AutomaticGameAction {
  override def execute(currentGameState: GameState): (GameState, Seq[GameAction]) = {
    val drawActions = Seq(DrawStartingHandsEvent(playersToDrawAndMulligan))
    val mulliganChoices = playersToDrawAndMulligan.map(MulliganChoice)
    val executeMulligansAction = ExecuteMulligansAction(mulligansSoFar)
    (currentGameState, drawActions ++ mulliganChoices :+ executeMulligansAction)
  }
}
