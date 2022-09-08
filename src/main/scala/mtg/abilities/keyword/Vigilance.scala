package mtg.abilities.keyword

import mtg.abilities.KeywordAbility
import mtg.continuousEffects.{ContinuousEffect, PreventionEffect}
import mtg.definitions.ObjectId
import mtg.effects.EffectContext
import mtg.game.state.{GameAction, GameState}
import mtg.game.turns.turnBasedActions.TapAttacker

case object Vigilance extends KeywordAbility {
  override def getEffects(effectContext: EffectContext): Seq[ContinuousEffect] = {
    Seq(VigilanceEffect(effectContext.thisObjectId))
  }
}

case class VigilanceEffect(affectedObject: ObjectId) extends PreventionEffect.SimpleCheck {
  override def shouldPreventAction(action: GameAction[_], gameState: GameState): Boolean = {
    action match {
      case TapAttacker(`affectedObject`) => true
      case _ => false
    }
  }
}
