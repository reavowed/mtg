package mtg.abilities.keyword

import mtg.abilities.{KeywordAbility, StaticAbility}
import mtg.continuousEffects.{ContinuousEffect, TargetPreventionEffect}
import mtg.core.{ObjectId, ObjectOrPlayerId}
import mtg.game.state.{GameState, ObjectWithState, StackObjectWithState}

case object Hexproof extends KeywordAbility {
  override def getEffects(objectWithAbility: ObjectWithState): Seq[ContinuousEffect] = Seq(HexproofEffect(objectWithAbility.gameObject.objectId))
}

case class HexproofEffect(affectedObject: ObjectId) extends TargetPreventionEffect {
  override def preventsTarget(source: StackObjectWithState, target: ObjectOrPlayerId, gameState: GameState): Boolean = {
    // TODO: In multiplayer games, other players are not necessarily opponents
    target == affectedObject && gameState.gameObjectState.derivedState.permanentStates(affectedObject).controller != source.controller
  }
}
