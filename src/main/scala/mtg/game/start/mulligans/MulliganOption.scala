package mtg.game.start.mulligans

sealed trait MulliganOption
object MulliganOption {
  case object Mulligan extends MulliganOption
  case object Keep extends MulliganOption
}
