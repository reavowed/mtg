package mtg.effects

import mtg.game.state.GameObjectEvent
import mtg.game.state.history.LogEvent

sealed trait EffectResult
object EffectResult {
  case class Event(gameObjectEvent: GameObjectEvent, resolutionContext: ResolutionContext) extends EffectResult
  case class Choice(effectChoice: EffectChoice) extends EffectResult
  case class Log(logEvent: LogEvent, resolutionContext: ResolutionContext) extends EffectResult

  implicit def eventToEffectResult(tuple: (GameObjectEvent, ResolutionContext)): EffectResult = Event(tuple._1, tuple._2)
  implicit def choiceToEffectResult(choice: EffectChoice): EffectResult = Choice(choice)
  implicit def logEventToEffectResult(tuple: (LogEvent, ResolutionContext)): EffectResult = Log(tuple._1, tuple._2)
}
