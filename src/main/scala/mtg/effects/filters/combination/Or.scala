package mtg.effects.filters.combination

import mtg.core.ObjectOrPlayerId
import mtg.effects.EffectContext
import mtg.effects.filters.Filter
import mtg.game.state.GameState
import mtg.utils.TextUtils._

case class Or[T <: ObjectOrPlayerId](innerFilters: Filter[T]*) extends Filter[T] {
  override def getSingular(cardName: String): String = innerFilters.map(_.getSingular(cardName)).toCommaList("or")
  override def getPlural(cardName: String): String = innerFilters.map(_.getPlural(cardName)).toCommaList("or")

  override def getAll(gameState: GameState, effectContext: EffectContext): Seq[T] = {
    innerFilters.flatMap(_.getAll(gameState, effectContext)).distinct
  }
}
