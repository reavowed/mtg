package mtg.abilities.keyword

import mtg.abilities.{KeywordAbility, StaticAbility}
import mtg.continuousEffects.{ContinuousEffect, TargetPreventionEffect}
import mtg.core.{ObjectId, ObjectOrPlayerId}
import mtg.effects.EffectContext
import mtg.game.state.{GameState, ObjectWithState, StackObjectWithState}

case object Hexproof extends KeywordAbility {
  override def getEffects(effectContext: EffectContext): Seq[ContinuousEffect] = {
    Seq(HexproofEffect(effectContext.thisObjectId))
  }
}

case class HexproofEffect(affectedObject: ObjectId) extends TargetPreventionEffect {
  override def preventsTarget(source: StackObjectWithState, target: ObjectOrPlayerId, gameState: GameState): Boolean = {
    // TODO: In multiplayer games, other players are not necessarily opponents
    target == affectedObject && gameState.gameObjectState.derivedState.permanentStates(affectedObject).controller != source.controller
  }
}
