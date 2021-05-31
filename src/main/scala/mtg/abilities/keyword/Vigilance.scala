package mtg.abilities.keyword

import mtg.abilities.{KeywordAbility, StaticAbility}
import mtg.effects.ContinuousEffect
import mtg.effects.continuous.EventPreventionEffect
import mtg.game.ObjectId
import mtg.game.state.{GameObjectEvent, GameState, ObjectWithState}
import mtg.game.turns.turnBasedActions.TapAttacker

case object Vigilance extends StaticAbility with KeywordAbility {
  override def getEffects(objectWithAbility: ObjectWithState): Seq[ContinuousEffect] = Seq(VigilanceEffect(objectWithAbility.gameObject.objectId))
}

case class VigilanceEffect(affectedObject: ObjectId) extends EventPreventionEffect {
  override def preventsEvent(gameObjectEvent: GameObjectEvent, gameState: GameState): Boolean = {
    gameObjectEvent match {
      case TapAttacker(`affectedObject`) => true
      case _ => false
    }
  }
}
