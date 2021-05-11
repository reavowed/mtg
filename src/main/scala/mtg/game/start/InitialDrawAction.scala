package mtg.game.start

import mtg.events.DrawCardsEvent
import mtg.game.GameData
import mtg.game.objects.GameObjectState
import mtg.game.state.{Action, HandleEventsAction, Transition}

case object InitialDrawAction extends Action {
  def runAction(currentGameObjectState: GameObjectState, gameData: GameData): (GameObjectState, Transition)  = {
    (currentGameObjectState, HandleEventsAction(gameData.playersInTurnOrder.map(DrawCardsEvent(_, 7)), PlayerMulligansAction))
  }
}
