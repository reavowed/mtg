package mtg.instructions

import mtg.effects.StackObjectResolutionContext
import mtg.game.state.{GameAction, InternalGameAction}
import mtg.game.state.history.LogEvent

sealed trait InstructionResult
object InstructionResult {
  case class Action(gameAction: GameAction[_], resolutionContext: StackObjectResolutionContext) extends InstructionResult
  case class Choice(instructionChoice: InstructionChoice) extends InstructionResult
  case class Log(logEvent: LogEvent, resolutionContext: StackObjectResolutionContext) extends InstructionResult
  case class UpdatedContext(resolutionContext: StackObjectResolutionContext) extends InstructionResult

  implicit def actionToInstructionResult(tuple: (GameAction[_], StackObjectResolutionContext)): InstructionResult = Action(tuple._1, tuple._2)
  implicit def choiceToInstructionResult(choice: InstructionChoice): InstructionResult = Choice(choice)
  implicit def logEventToInstructionResult(tuple: (LogEvent, StackObjectResolutionContext)): InstructionResult = Log(tuple._1, tuple._2)
  implicit def contextToInstructionResult(resolutionContext: StackObjectResolutionContext): InstructionResult = UpdatedContext(resolutionContext)
}
