package mtg.game.state

import mtg.game.GameData
import mtg.game.objects.GameObjectState

sealed abstract class Transition

abstract class Action extends Transition {
  def runAction(currentGameObjectState: GameObjectState, gameData: GameData): (GameObjectState, Transition)
}

abstract class Choice extends Transition {
  def handleDecision(serializedAction: String, currentGameObjectState: GameObjectState, gameData: GameData): Transition
}

sealed abstract class GameResult extends Transition
object GameResult {
  object Tie extends GameResult
}

