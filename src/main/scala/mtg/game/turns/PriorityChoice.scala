package mtg.game.turns

import mtg.game.PlayerIdentifier
import mtg.game.state.{GameAction, GameState, Option, TypedChoice}

trait PriorityOption extends Option

case class PriorityChoice(playerToAct: PlayerIdentifier) extends TypedChoice[PriorityOption] {
  override def parseOption: PartialFunction[String, PriorityOption] = ???
  override def handleDecision(chosenOption: PriorityOption, currentGameState: GameState): (GameState, Seq[GameAction]) = ???
}
