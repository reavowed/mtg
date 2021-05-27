package mtg.game.actions.cast

import mtg.events.MoveObjectEvent
import mtg.game.actions.SpendManaAutomaticallyEvent
import mtg.game.objects.{GameObject, ManaObject}
import mtg.game.state.history.LogEvent
import mtg.game.state._
import mtg.game.turns.priority
import mtg.game.turns.priority.PriorityFromPlayerAction
import mtg.game.{PlayerIdentifier, Zone}
import mtg.parts.costs.{GenericManaSymbol, ManaSymbol, ManaTypeSymbol}

import scala.annotation.tailrec

object CastSpellSteps {

  case class Start(player: PlayerIdentifier, objectToCast: ObjectWithState, backupAction: BackupAction) extends InternalGameAction {
    override def execute(currentGameState: GameState): InternalGameActionResult = {
      Seq(
        MoveObjectEvent(player, objectToCast.gameObject, Zone.Stack),
        PayCosts(player, backupAction))
    }
  }

  case class PayCosts(player: PlayerIdentifier, backupAction: BackupAction) extends InternalGameAction {
    @tailrec
    private def payDirectManaCosts(manaTypeSymbols: Seq[ManaTypeSymbol], manaInPool: Seq[ManaObject]): Option[Seq[ManaObject]] = {
      manaTypeSymbols match {
        case symbol +: remainingSymbols =>
          manaInPool.zipWithIndex.findIndex(_._1.manaType == symbol.manaType).map(manaInPool.removeAtIndex) match {
            case Some(remainingManaInPool) =>
              payDirectManaCosts(remainingSymbols, remainingManaInPool)
            case None =>
              None
          }
        case Nil =>
          Some(manaInPool)
      }
    }

    private def payGenericManaCosts(genericManaSymbols: Seq[GenericManaSymbol], manaInPool: Seq[ManaObject]): Option[Seq[ManaObject]] = {
      if (genericManaSymbols.map(_.amount).sum == manaInPool.length) {
        Some(Nil)
      } else {
        None
      }
    }

    private def payManaAutomatically(manaSymbols: Seq[ManaSymbol], manaInPool: Seq[ManaObject]): Option[Seq[ManaObject]] = {
      val (manaTypeSymbols, nonManaTypeSymbols) = manaSymbols.splitByType[ManaTypeSymbol]
      for {
        manaAfterSymbols <- payDirectManaCosts(manaTypeSymbols, manaInPool)
        (genericManaSymbols, otherSymbols) = nonManaTypeSymbols.splitByType[GenericManaSymbol]
        manaAfterGeneric <- payGenericManaCosts(genericManaSymbols, manaAfterSymbols)
        if otherSymbols.isEmpty
      } yield manaAfterGeneric
    }

    override def execute(currentGameState: GameState): InternalGameActionResult = {
      val spell = currentGameState.gameObjectState.stack.last
      val spellWithState = currentGameState.derivedState.objectStates(spell.objectId)
      spellWithState.characteristics.manaCost match {
        case Some(cost) =>
          val initialManaInPool = currentGameState.gameObjectState.manaPools(player)
          val finalManaInPool = payManaAutomatically(cost.symbols, initialManaInPool)
          finalManaInPool match {
            case Some(finalManaInPool) =>
              Seq(SpendManaAutomaticallyEvent(player, finalManaInPool), FinishCasting(player, spell))
            case None =>
              backupAction
          }
        case None =>
          backupAction
      }
    }
  }

  case class FinishCasting(player: PlayerIdentifier, spell: GameObject) extends InternalGameAction {
    override def execute(currentGameState: GameState): InternalGameActionResult = {
      val spellWithState = currentGameState.derivedState.objectStates(spell.objectId)
      InternalGameActionResult(
        Seq(SpellCastEvent(spell), priority.PriorityFromPlayerAction(player)),
        Some(LogEvent.CastSpell(player, spellWithState.characteristics.name)))
    }
  }

}
