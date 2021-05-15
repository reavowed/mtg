package mtg.game.start.mulligans

import mtg.game.state.GameOption

sealed trait MulliganOption extends GameOption
object MulliganOption {
  object Mulligan extends MulliganOption
  object Keep extends MulliganOption
}
