package mtg.game.start.mulligans

import mtg.game.state.GameOption

sealed trait MulliganOption extends GameOption
object MulliganOption {
  case object Mulligan extends MulliganOption
  case object Keep extends MulliganOption
}
