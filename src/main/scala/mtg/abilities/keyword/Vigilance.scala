package mtg.abilities.keyword

import mtg.abilities.KeywordAbility
import mtg.continuousEffects.{ContinuousEffect, PreventionEffect}
import mtg.core.ObjectId
import mtg.game.state.{GameAction, GameState, ObjectWithState}
import mtg.game.turns.turnBasedActions.TapAttacker

case object Vigilance extends KeywordAbility {
  override def getEffects(objectWithAbility: ObjectWithState): Seq[ContinuousEffect] = Seq(VigilanceEffect(objectWithAbility.gameObject.objectId))
}

case class VigilanceEffect(affectedObject: ObjectId) extends PreventionEffect.SimpleCheck {
  override def shouldPreventAction(action: GameAction[_], gameState: GameState): Boolean = {
    action match {
      case TapAttacker(`affectedObject`) => true
      case _ => false
    }
  }
}
