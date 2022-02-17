package mtg.actions

import mtg.core.{ManaType, PlayerId}
import mtg.core.symbols.ManaSymbol
import mtg.game.objects.ManaObject
import mtg.game.state.{GameActionResult, GameState, InternalGameAction}

case class AddManaEvent(player: PlayerId, manaSymbols: Seq[ManaSymbol]) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.updateManaPool(player, _ ++ manaSymbols.flatMap(getManaObjects))
  }
  override def canBeReverted: Boolean = true

  private def getManaObjects(manaSymbol: ManaSymbol): Seq[ManaObject] = {
    manaSymbol match {
      case ManaSymbol.ForType(manaType) => Seq(ManaObject(manaType))
      case ManaSymbol.Generic(amount) => Seq.fill(amount)(ManaObject(ManaType.Colorless))
    }
  }
}
