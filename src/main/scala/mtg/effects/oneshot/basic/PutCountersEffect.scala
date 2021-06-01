package mtg.effects.oneshot.basic

import mtg.effects.OneShotEffect
import mtg.parts.counters.CounterType
import mtg.effects.identifiers.Identifier
import mtg.effects.oneshot.{OneShotEffectResolutionContext, OneShotEffectResult}
import mtg.events.PutCountersEvent
import mtg.game.ObjectId
import mtg.game.state.GameState
import mtg.utils.TextUtils

case class PutCountersEffect(number: Int, kind: CounterType, objectIdentifier: Identifier[ObjectId]) extends OneShotEffect {
  override def getText(cardName: String): String = {
    def counterDescription = kind.description
    def numberWord = TextUtils.getWord(number, counterDescription)
    def counterWord = if (number == 1) "counter" else "counters"
    Seq("put", numberWord, counterDescription, counterWord, "on", objectIdentifier.getText(cardName)).mkString(" ")
  }

  override def resolve(gameState: GameState, resolutionContext: OneShotEffectResolutionContext): OneShotEffectResult = {
    val (obj, resultContext) = objectIdentifier.get(gameState, resolutionContext)
    (PutCountersEvent(number, kind, obj), resultContext)
  }
}