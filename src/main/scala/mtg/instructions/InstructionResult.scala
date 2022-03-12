package mtg.instructions

import mtg.effects.StackObjectResolutionContext
import mtg.game.state.InternalGameAction
import mtg.game.state.history.LogEvent

sealed trait InstructionResult
object InstructionResult {
  case class Event(InternalGameAction: InternalGameAction, resolutionContext: StackObjectResolutionContext) extends InstructionResult
  case class Choice(instructionChoice: InstructionChoice) extends InstructionResult
  case class Log(logEvent: LogEvent, resolutionContext: StackObjectResolutionContext) extends InstructionResult

  implicit def eventToInstructionResult(tuple: (InternalGameAction, StackObjectResolutionContext)): InstructionResult = Event(tuple._1, tuple._2)
  implicit def choiceToInstructionResult(choice: InstructionChoice): InstructionResult = Choice(choice)
  implicit def logEventToInstructionResult(tuple: (LogEvent, StackObjectResolutionContext)): InstructionResult = Log(tuple._1, tuple._2)
}
