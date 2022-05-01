package mtg.instructions

import mtg.actions.moveZone.MoveToBattlefieldAction

trait EntersTheBattlefieldModifier extends TextComponent {
  def modifyAction(action: MoveToBattlefieldAction): MoveToBattlefieldAction
}
