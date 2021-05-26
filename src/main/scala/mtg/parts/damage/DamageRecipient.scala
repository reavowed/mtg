package mtg.parts.damage

import mtg.events.LoseLifeEvent
import mtg.game.PlayerIdentifier
import mtg.game.objects.ObjectId
import mtg.game.state.GameObjectEvent

sealed trait DamageRecipient {
  def getDamageResult(dealDamageEvent: DealDamageEvent): Seq[GameObjectEvent]
}
object DamageRecipient {
  case class Creature(creatureId: ObjectId) extends DamageRecipient {
    override def getDamageResult(dealDamageEvent: DealDamageEvent): Seq[GameObjectEvent] = Seq(MarkDamageEvent(dealDamageEvent.source, creatureId, dealDamageEvent.amount))
  }
  case class Player(player: PlayerIdentifier) extends DamageRecipient {
    override def getDamageResult(dealDamageEvent: DealDamageEvent): Seq[GameObjectEvent] = Seq(LoseLifeEvent(player, dealDamageEvent.amount))
  }
}
