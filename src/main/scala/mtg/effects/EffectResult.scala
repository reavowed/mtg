package mtg.effects

import mtg.game.state.GameObjectEvent
import mtg.game.state.history.LogEvent

sealed trait EffectResult
object EffectResult {
  case class Event(gameObjectEvent: GameObjectEvent) extends EffectResult
  case class Choice(effectChoice: EffectChoice) extends EffectResult
  case class Log(logEvent: LogEvent) extends EffectResult

  implicit def eventToEffectResult(gameObjectEvent: GameObjectEvent): EffectResult = Event(gameObjectEvent)
  implicit def choiceToEffectResult(choice: EffectChoice): EffectResult = Choice(choice)
  implicit def logEventToEffectResult(logEvent: LogEvent): EffectResult = Log(logEvent)
}
