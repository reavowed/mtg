package mtg.game.turns.turnBasedActions

import mtg._
import mtg.characteristics.types.Type
import mtg.events.TapObjectEvent
import mtg.game.{ObjectId, PlayerId}
import mtg.game.state.history.GameEvent.{Decision, ResolvedEvent}
import mtg.game.state.history.LogEvent
import mtg.game.state._

object DeclareAttackers extends InternalGameAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = {
    val possibleAttackers = getPossibleAttackers(currentGameState)
    if (possibleAttackers.nonEmpty)
      DeclareAttackersChoice(
        currentGameState.activePlayer,
        getDefendingPlayer(currentGameState),
        possibleAttackers)
    else
      ()
  }
  private def wasContinuouslyControlled(objectId: ObjectId, gameState: GameState): Boolean = {
    gameState.gameHistory.forCurrentTurn.exists(
      _.gameEvents.ofType[ResolvedEvent].forall(
        _.stateAfterwards.permanentStates.get(objectId)
          .exists(_.controller == gameState.activePlayer)))
  }
  private def getPossibleAttackers(gameState: GameState): Seq[ObjectId] = {
    gameState.gameObjectState.derivedState.permanentStates.values.view
      .filter(o => o.characteristics.types.contains(Type.Creature))
      .filter(o => o.controller == gameState.activePlayer)
      .filter(o => !o.gameObject.status.isTapped)
      .filter(o => wasContinuouslyControlled(o.gameObject.objectId, gameState))
      .map(o => o.gameObject.objectId)
      .toSeq
  }

  def getDefendingPlayer(gameState: GameState): PlayerId = {
    gameState.playersInApnapOrder.filter(_ != gameState.activePlayer).single
  }
  def getAttackDeclarations(gameState: GameState): Seq[AttackDeclaration] = {
    gameState.gameHistory.forCurrentTurn
      .flatMap(
        _.gameEvents.view.ofType[Decision]
          .map(_.chosenOption)
          .mapFind(_.asOptionalInstanceOf[DeclaredAttackers]))
      .toSeq
      .flatMap(_.attackDeclarations)
  }
}

case class AttackDeclaration(attacker: ObjectId, attackedPlayer: PlayerId)
case class DeclaredAttackers(attackDeclarations: Seq[AttackDeclaration])

case class DeclareAttackersChoice(playerToAct: PlayerId, defendingPlayer: PlayerId, possibleAttackers: Seq[ObjectId]) extends TypedPlayerChoice[DeclaredAttackers] {
  override def parseOption(serializedChosenOption: String, currentGameState: GameState): Option[DeclaredAttackers] = {
    serializedChosenOption
      .split(" ").toSeq
      .filter(_.nonEmpty)
      .map(_.toIntOption.flatMap(i => possibleAttackers.find(_.sequentialId == i)).map(AttackDeclaration(_, defendingPlayer)))
      .swap
      .map(DeclaredAttackers)
  }

  override def handleDecision(chosenOption: DeclaredAttackers, currentGameState: GameState): (Seq[GameAction], Option[LogEvent]) = {
    import chosenOption._
    if (attackDeclarations.nonEmpty) {
      (
        Seq(TapAttackers(attackDeclarations.map(_.attacker))),
        Some(LogEvent.DeclareAttackers(
          playerToAct,
          attackDeclarations.map(_.attacker.currentCharacteristics(currentGameState).name)
        ))
      )
    } else {
      (Nil, None)
    }
  }
}

case class TapAttackers(attackers: Seq[ObjectId]) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    attackers.map(TapObjectEvent)
  }
}
