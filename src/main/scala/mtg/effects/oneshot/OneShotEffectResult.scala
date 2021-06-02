package mtg.effects.oneshot

import mtg.game.state.GameObjectEvent
import mtg.game.state.history.LogEvent

sealed trait OneShotEffectResult
object OneShotEffectResult {
  case class Event(gameObjectEvent: GameObjectEvent, resolutionContext: OneShotEffectResolutionContext) extends OneShotEffectResult
  case class Choice(effectChoice: OneShotEffectChoice) extends OneShotEffectResult
  case class Log(logEvent: LogEvent, resolutionContext: OneShotEffectResolutionContext) extends OneShotEffectResult
  case class OnlyContext(resolutionContext: OneShotEffectResolutionContext) extends OneShotEffectResult

  implicit def eventToEffectResult(tuple: (GameObjectEvent, OneShotEffectResolutionContext)): OneShotEffectResult = Event(tuple._1, tuple._2)
  implicit def choiceToEffectResult(choice: OneShotEffectChoice): OneShotEffectResult = Choice(choice)
  implicit def logEventToEffectResult(tuple: (LogEvent, OneShotEffectResolutionContext)): OneShotEffectResult = Log(tuple._1, tuple._2)
  implicit def contextToEffectResult(context: OneShotEffectResolutionContext): OneShotEffectResult = OnlyContext(context)
}
