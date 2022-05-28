package mtg.instructions

import mtg.actions.moveZone.MoveToBattlefieldAction
import mtg.effects.EffectContext

trait EntersTheBattlefieldModifier extends TextComponent {
  def modifyAction(action: MoveToBattlefieldAction, effectContext: EffectContext): MoveToBattlefieldAction
}
