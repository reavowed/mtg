package mtg.effects

import mtg.game.PlayerIdentifier
import mtg.game.state.GameAction

abstract class Effect {
  def text: String
  def resolveForAbility(controller: PlayerIdentifier): Seq[GameAction]
}
