package mtg.game.start.mulligans

import mtg.game.state.ChoiceOption

sealed trait MulliganOption extends ChoiceOption
object MulliganOption {
  case object Mulligan extends MulliganOption
  case object Keep extends MulliganOption
}
