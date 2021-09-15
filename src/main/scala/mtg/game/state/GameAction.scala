package mtg.game.state

import mtg.game.{ObjectId, PlayerId, Zone}

sealed trait GameAction

trait InternalGameAction extends GameAction {
  def execute(gameState: GameState): GameActionResult
  def canBeReverted: Boolean
}

case class BackupAction(gameStateToRevertTo: GameState) extends GameAction

trait Choice extends GameAction {
  def playerToAct: PlayerId
  def parseDecision(serializedDecision: String): Option[Decision]
  def temporarilyVisibleZones: Seq[Zone] = Nil
  def temporarilyVisibleObjects: Seq[ObjectId] = Nil
}
object Choice {
  trait WithParser extends Choice {
    def parser: PartialFunction[String, Decision]
    override def parseDecision(serializedDecision: String): Option[Decision] = parser.lift(serializedDecision)
  }
}

case class Decision(resultingActions: Seq[InternalGameAction])
object Decision {
  implicit def single(action: InternalGameAction): Decision = Decision(Seq(action))
  implicit def singleOption(action: InternalGameAction): Option[Decision] = Some(Decision(Seq(action)))
  implicit def multiple(actions: Seq[InternalGameAction]): Decision = Decision(actions)
  implicit def chain[T, S](f: T => S)(implicit g: S => Decision): T => Decision = t => g(f(t))
}

sealed abstract class GameResult extends GameAction
object GameResult {
  object Tie extends GameResult
}

