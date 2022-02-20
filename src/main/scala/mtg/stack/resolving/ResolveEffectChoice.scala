package mtg.stack.resolving

import mtg.core.zones.Zone
import mtg.core.{ObjectId, PlayerId}
import mtg.effects.oneshot.OneShotEffectChoice
import mtg.effects.{OneShotEffect, StackObjectResolutionContext}
import mtg.game.state.{Choice, GameState, InternalGameAction}

case class ResolveEffectChoice(effectChoice: OneShotEffectChoice, remainingEffects: Seq[OneShotEffect]) extends Choice[(Option[InternalGameAction], StackObjectResolutionContext)] {
  override def playerToAct: PlayerId = effectChoice.playerChoosing

  def handleDecision(serializedDecision: String)(implicit gameState: GameState): Option[(Option[InternalGameAction], StackObjectResolutionContext)] = {
    effectChoice.parseDecision(serializedDecision)
  }

  override def temporarilyVisibleZones: Seq[Zone] = effectChoice.temporarilyVisibleZones

  override def temporarilyVisibleObjects: Seq[ObjectId] = effectChoice.temporarilyVisibleObjects
}
