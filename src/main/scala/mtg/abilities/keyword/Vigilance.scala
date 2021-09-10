package mtg.abilities.keyword

import mtg.abilities.{KeywordAbility, StaticAbility}
import mtg.effects.ContinuousObjectEffect
import mtg.game.ObjectId
import mtg.game.state.{AutomaticGameAction, GameState, ObjectWithState}
import mtg.game.turns.GameActionPreventionEffect
import mtg.game.turns.turnBasedActions.TapAttacker

case object Vigilance extends StaticAbility with KeywordAbility {
  override def getEffects(objectWithAbility: ObjectWithState): Seq[ContinuousObjectEffect] = Seq(VigilanceEffect(objectWithAbility.gameObject.objectId))
}

case class VigilanceEffect(affectedObject: ObjectId) extends GameActionPreventionEffect.SimpleObject {
  override def preventsEvent(gameAction: AutomaticGameAction, gameState: GameState): Boolean = {
    gameAction match {
      case TapAttacker(`affectedObject`) => true
      case _ => false
    }
  }
}
