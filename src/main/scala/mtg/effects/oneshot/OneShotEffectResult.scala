package mtg.effects.oneshot

import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameObjectEvent
import mtg.game.state.history.LogEvent

sealed trait OneShotEffectResult
object OneShotEffectResult {
  case class Event(gameObjectEvent: GameObjectEvent, resolutionContext: StackObjectResolutionContext) extends OneShotEffectResult
  case class Choice(effectChoice: OneShotEffectChoice) extends OneShotEffectResult
  case class Log(logEvent: LogEvent, resolutionContext: StackObjectResolutionContext) extends OneShotEffectResult
  case class OnlyContext(resolutionContext: StackObjectResolutionContext) extends OneShotEffectResult

  implicit def eventToEffectResult(tuple: (GameObjectEvent, StackObjectResolutionContext)): OneShotEffectResult = Event(tuple._1, tuple._2)
  implicit def choiceToEffectResult(choice: OneShotEffectChoice): OneShotEffectResult = Choice(choice)
  implicit def logEventToEffectResult(tuple: (LogEvent, StackObjectResolutionContext)): OneShotEffectResult = Log(tuple._1, tuple._2)
  implicit def contextToEffectResult(context: StackObjectResolutionContext): OneShotEffectResult = OnlyContext(context)
}
