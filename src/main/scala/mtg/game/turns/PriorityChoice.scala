package mtg.game.turns

import mtg.game.PlayerIdentifier
import mtg.game.state.{GameAction, GameState, GameOption, TypedChoice}

trait PriorityOption extends GameOption

case class PriorityChoice(playerToAct: PlayerIdentifier) extends TypedChoice[PriorityOption] {
  override def parseOption(serializedChosenOption: String, currentGameState: GameState): Option[PriorityOption] = ???
  override def handleDecision(chosenOption: PriorityOption, currentGameState: GameState): Seq[GameAction] = ???
}
