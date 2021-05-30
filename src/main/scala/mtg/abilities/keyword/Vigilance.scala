package mtg.abilities.keyword

import mtg.abilities.{KeywordAbility, StaticAbility}
import mtg.effects.continuous.{ContinuousEffect, EventPreventionEffect}
import mtg.game.ObjectId
import mtg.game.state.{GameObjectEvent, GameState, ObjectWithState}
import mtg.game.turns.turnBasedActions.TapAttacker

case object Vigilance extends StaticAbility with KeywordAbility {
  override def getEffects(objectWithAbility: ObjectWithState): Seq[ContinuousEffect] = Seq(VigilanceEffect(objectWithAbility.gameObject.objectId))
}

case class VigilanceEffect(objectWithAbility: ObjectId) extends EventPreventionEffect {
  override def preventsEvent(gameObjectEvent: GameObjectEvent, gameState: GameState): Boolean = {
    gameObjectEvent match {
      case TapAttacker(`objectWithAbility`) => true
      case _ => false
    }
  }
}
