package mtg.abilities.keyword

import mtg.abilities.{KeywordAbility, StaticAbility}
import mtg.effects.ContinuousEffect
import mtg.effects.continuous.PreventionEffect
import mtg.game.ObjectId
import mtg.game.state.{GameState, InternalGameAction, ObjectWithState}
import mtg.game.turns.turnBasedActions.TapAttacker

case object Vigilance extends StaticAbility with KeywordAbility {
  override def getEffects(objectWithAbility: ObjectWithState): Seq[ContinuousEffect] = Seq(VigilanceEffect(objectWithAbility.gameObject.objectId))
}

case class VigilanceEffect(affectedObject: ObjectId) extends PreventionEffect.SimpleCheck with ContinuousEffect.ForSingleObject {
  override def shouldPreventAction(action: InternalGameAction, gameState: GameState): Boolean = {
    action match {
      case TapAttacker(`affectedObject`) => true
      case _ => false
    }
  }
}
