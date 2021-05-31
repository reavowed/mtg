package mtg.game.actions.cast

import mtg.abilities.SpellAbility
import mtg.effects.targets.TargetIdentifier
import mtg.events.MoveObjectEvent
import mtg.events.targets.AddTarget
import mtg.game.actions.SpendManaAutomaticallyEvent
import mtg.game.objects.{GameObject, ManaObject, StackObject}
import mtg.game.state.history.LogEvent
import mtg.game.state._
import mtg.game.turns.priority
import mtg.game.{ObjectId, ObjectOrPlayer, PlayerId, Zone}
import mtg.parts.costs.{GenericManaSymbol, ManaSymbol, ManaTypeSymbol}

import scala.annotation.tailrec

object CastSpellSteps {

  case class Start(player: PlayerId, objectToCast: ObjectId, backupAction: BackupAction) extends InternalGameAction {
    override def execute(currentGameState: GameState): InternalGameActionResult = {
      Seq(
        MoveObjectEvent(player, objectToCast, Zone.Stack),
        ChooseTargets(backupAction),
        PayCosts(player, backupAction))
    }
  }

  case class ChooseTargets(backupAction: BackupAction) extends InternalGameAction {
    override def execute(currentGameState: GameState): InternalGameActionResult = {
      val spell = currentGameState.gameObjectState.stack.last
      val spellWithState = spell.currentState(currentGameState)
      val targetIdentifiers = spellWithState.characteristics.abilities.ofType[SpellAbility].flatMap(_.effects).flatMap(_.targetIdentifiers)
      targetIdentifiers.map(targetIdentifier => TargetChoice(spellWithState.controller, spell, targetIdentifier.text, targetIdentifier.getValidChoices(currentGameState)))
    }
  }

  case class ChosenTarget(objectOrPlayer: ObjectOrPlayer)
  case class TargetChoice(playerToAct: PlayerId, spell: StackObject, targetDescription: String, validOptions: Seq[ObjectOrPlayer]) extends TypedPlayerChoice[ChosenTarget] {
    override def parseOption(serializedChosenOption: String, currentGameState: GameState): Option[ChosenTarget] = {
      validOptions.find(_.toString == serializedChosenOption).map(ChosenTarget)
    }
    override def handleDecision(chosenOption: ChosenTarget, currentGameState: GameState): (Seq[GameAction], Option[LogEvent]) = {
      (Seq(AddTarget(spell.objectId, chosenOption.objectOrPlayer)), None)
    }
  }

  case class PayCosts(player: PlayerId, backupAction: BackupAction) extends InternalGameAction {
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
      val spellWithState = spell.currentState(currentGameState)
      spellWithState.characteristics.manaCost match {
        case Some(cost) =>
          val initialManaInPool = currentGameState.gameObjectState.manaPools(player)
          val finalManaInPool = payManaAutomatically(cost.symbols, initialManaInPool)
          finalManaInPool match {
            case Some(finalManaInPool) =>
              Seq(SpendManaAutomaticallyEvent(player, finalManaInPool), FinishCasting(player))
            case None =>
              backupAction
          }
        case None =>
          backupAction
      }
    }
  }

  case class FinishCasting(player: PlayerId) extends InternalGameAction {
    override def execute(currentGameState: GameState): InternalGameActionResult = {
      val spell = currentGameState.gameObjectState.stack.last
      val spellWithState = spell.currentState(currentGameState)
      InternalGameActionResult(
        Seq(SpellCastEvent(spell), priority.PriorityFromPlayerAction(player)),
        Some(LogEvent.CastSpell(player, spellWithState.characteristics.name, spellWithState.gameObject.targets.map(_.getName(currentGameState)))))
    }
  }

}
