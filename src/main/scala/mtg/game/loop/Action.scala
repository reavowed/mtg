package mtg.game.loop

abstract class Action {
  def execute(): Either[Action, Either[Choice, GameResult]]
}
