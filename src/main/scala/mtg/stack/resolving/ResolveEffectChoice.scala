package mtg.stack.resolving

import mtg.core.{ObjectId, PlayerId}
import mtg.effects.OneShotEffect
import mtg.effects.oneshot.OneShotEffectChoice
import mtg.game.state.{Choice, Decision}
import mtg.game.Zone

case class ResolveEffectChoice(effectChoice: OneShotEffectChoice, remainingEffects: Seq[OneShotEffect]) extends Choice {
  override def playerToAct: PlayerId = effectChoice.playerChoosing

  override def parseDecision(serializedDecision: String): Option[Decision] = {
    effectChoice.parseDecision(serializedDecision)
      .map { case (actionOption, newResolutionContext) =>
        actionOption.toSeq :+ ResolveEffects(remainingEffects, newResolutionContext)
      }
  }

  override def temporarilyVisibleZones: Seq[Zone] = effectChoice.temporarilyVisibleZones

  override def temporarilyVisibleObjects: Seq[ObjectId] = effectChoice.temporarilyVisibleObjects
}
