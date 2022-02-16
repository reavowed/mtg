package mtg.game.start

import mtg.core.PlayerId

sealed trait MulliganDecision {
  def player: PlayerId
}

object MulliganDecision {
  case class Keep(player: PlayerId) extends MulliganDecision
  case class Mulligan(player: PlayerId) extends MulliganDecision
}
