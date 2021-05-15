package mtg.game.start.mulligans

import mtg.game.state.Option

sealed trait MulliganOption extends Option
object MulliganOption {
  object Mulligan extends MulliganOption
  object Keep extends MulliganOption
}
