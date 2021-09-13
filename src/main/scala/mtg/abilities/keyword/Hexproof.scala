package mtg.abilities.keyword

import mtg.abilities.{KeywordAbility, StaticAbility}
import mtg.effects.ContinuousEffect
import mtg.effects.continuous.TargetPreventionEffect
import mtg.game.state.{GameState, ObjectWithState, StackObjectWithState}
import mtg.game.{ObjectId, ObjectOrPlayer}

case object Hexproof extends StaticAbility with KeywordAbility {
  override def getEffects(objectWithAbility: ObjectWithState): Seq[ContinuousEffect] = Seq(HexproofEffect(objectWithAbility.gameObject.objectId))
}

case class HexproofEffect(affectedObject: ObjectId) extends TargetPreventionEffect with ContinuousEffect.ForSingleObject {
  override def preventsTarget(source: StackObjectWithState, target: ObjectOrPlayer, gameState: GameState): Boolean = {
    // TODO: In multiplayer games, other players are not necessarily opponents
    target == affectedObject && gameState.gameObjectState.derivedState.permanentStates(affectedObject).controller != source.controller
  }
}
