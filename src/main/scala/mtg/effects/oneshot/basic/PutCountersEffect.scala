package mtg.effects.oneshot.basic

import mtg.effects.identifiers.Identifier
import mtg.effects.oneshot.OneShotEffectResult
import mtg.effects.{OneShotEffect, StackObjectResolutionContext}
import mtg.events.PutCountersEvent
import mtg.game.ObjectId
import mtg.game.state.GameState
import mtg.parts.counters.CounterType
import mtg.utils.TextUtils

case class PutCountersEffect(number: Int, kind: CounterType, objectIdentifier: Identifier[ObjectId]) extends OneShotEffect {
  override def getText(cardName: String): String = {
    def counterDescription = kind.description
    def numberWord = TextUtils.getWord(number, counterDescription)
    def counterWord = if (number == 1) "counter" else "counters"
    Seq("put", numberWord, counterDescription, counterWord, "on", objectIdentifier.getText(cardName)).mkString(" ")
  }

  override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): OneShotEffectResult = {
    val (obj, resultContext) = objectIdentifier.get(gameState, resolutionContext)
    (PutCountersEvent(number, kind, obj), resultContext)
  }
}
