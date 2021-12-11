package mtg.game.state

import mtg.game.{GameStartingData, PlayerId}
import mtg.game.turns.TurnPhase.{PostcombatMainPhase, PrecombatMainPhase}
import mtg.game.turns.priority.PriorityChoice

case class Stops(stepOrPhaseByActivePlayerByPlayerWithStop: Map[PlayerId, Map[PlayerId, Seq[AnyRef]]]) {
  def shouldAutoPass(priorityChoice: PriorityChoice, gameState: GameState): Boolean = {
    !hasStop(priorityChoice.playerToAct, gameState.activePlayer, gameState.currentStep.orElse(gameState.currentPhase)) &&
      gameState.gameObjectState.stack.isEmpty
  }
  def hasStop(playerWithStop: PlayerId, activePlayer: PlayerId, stepOrPhase: Option[AnyRef]): Boolean = {
    stepOrPhaseByActivePlayerByPlayerWithStop(playerWithStop)(activePlayer).exists(stepOrPhase.contains)
  }

  def apply(playerWithStop: PlayerId): Map[PlayerId, Seq[AnyRef]] = stepOrPhaseByActivePlayerByPlayerWithStop(playerWithStop)
  def set(playerWithStop: PlayerId, activePlayer: PlayerId, stepOrPhase: AnyRef): Stops = {
    Stops(
      stepOrPhaseByActivePlayerByPlayerWithStop.updatedWith(playerWithStop)(_.map(
        _.updatedWith(activePlayer)(_.map(existingStops =>
          if (existingStops.contains(stepOrPhase)) existingStops else existingStops :+ stepOrPhase
        ))
      ))
    )
  }
  def unset(playerWithStop: PlayerId, activePlayer: PlayerId, stepOrPhase: AnyRef): Stops = {
    Stops(
      stepOrPhaseByActivePlayerByPlayerWithStop.updatedWith(playerWithStop)(_.map(
        _.updatedWith(activePlayer)(_.map(existingStops =>
          existingStops.filter(_ != stepOrPhase)
        ))
      ))
    )
  }
}

object Stops {
  def default(gameStartingData: GameStartingData): Stops = Stops(
    gameStartingData.players.map(playerWithStop =>
      playerWithStop -> gameStartingData.players.map(activePlayer =>
        activePlayer -> (if (playerWithStop == activePlayer) Seq(PrecombatMainPhase, PostcombatMainPhase) else Nil)
      ).toMap
    ).toMap
  )
}
