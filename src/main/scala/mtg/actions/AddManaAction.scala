package mtg.actions

import mtg.core.symbols.ManaSymbol
import mtg.core.{ManaType, PlayerId}
import mtg.game.state.{DirectGameObjectAction, GameState}

case class AddManaAction(player: PlayerId, manaSymbols: Seq[ManaSymbol]) extends DirectGameObjectAction[Unit] {
  override def execute(implicit gameState: GameState): DirectGameObjectAction.Result[Unit] = {
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
