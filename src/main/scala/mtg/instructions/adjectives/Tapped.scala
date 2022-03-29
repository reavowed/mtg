package mtg.instructions.adjectives

import mtg.core.ObjectId
import mtg.effects.EffectContext
import mtg.game.state.{CurrentCharacteristics, GameState}

object Tapped extends Adjective {
  override def getText(cardName: String): String = "tapped"
  override def describes(obj: ObjectId, gameState: GameState, effectContext: EffectContext): Boolean = {
    CurrentCharacteristics.getPermanentObject(obj, gameState).exists(_.status.isTapped)
  }
}
