package mtg.actions

import mtg.core.{ManaType, PlayerId}
import mtg.core.symbols.ManaSymbol
import mtg.game.objects.ManaObject
import mtg.game.state.{GameActionResult, GameState, InternalGameAction}

case class AddManaAction(player: PlayerId, manaSymbols: Seq[ManaSymbol]) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    manaSymbols.flatMap(getManaTypes).foldLeft(gameState.gameObjectState)(_.addMana(player, _))
  }
  override def canBeReverted: Boolean = true

  private def getManaTypes(manaSymbol: ManaSymbol): Seq[ManaType] = {
    manaSymbol match {
      case ManaSymbol.ForType(manaType) => Seq(manaType)
      case ManaSymbol.Generic(amount) => Seq.fill(amount)(ManaType.Colorless)
    }
  }
}
