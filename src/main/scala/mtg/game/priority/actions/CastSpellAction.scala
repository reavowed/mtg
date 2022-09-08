package mtg.game.priority.actions

import mtg.actions.moveZone.MoveToStackAction
import mtg.definitions.types.Type
import mtg.definitions.zones.ZoneType
import mtg.definitions.{ObjectId, PlayerId}
import mtg.game.state.{GameAction, GameState, ObjectWithState}
import mtg.stack.adding._

case class CastSpellAction(playerId: PlayerId, objectToCast: ObjectWithState) extends PriorityAction {
  override def objectId: ObjectId = objectToCast.gameObject.objectId
  override def displayText: String = "Cast"
  override def optionText: String = "Cast " + objectId

  override def delegate(implicit gameState: GameState): GameAction[Unit] = {
      for {
        spellId <- MoveToStackAction(objectId, playerId).map(_.get)
        _ <- ChooseModes(spellId)
        _ <- ChooseTargets(spellId)
        _ <- PayManaCosts.ForSpell(spellId)
        _ <- FinishCasting(playerId, spellId)
      } yield ()
  }
}

object CastSpellAction {
  def getCastableSpells(player: PlayerId, gameState: GameState): Seq[CastSpellAction] = {
    if (cannotCastSpells(player, gameState)) {
      return Nil
    }
    gameState.gameObjectState.derivedState.allObjectStates.values.view
      .filter(!cannotCast(player, gameState, _))
      .filter(hasGeneralPermissionToCastSpell(player, gameState, _))
      .filter(hasTimingPermissionToCastSpell(player, gameState, _))
      .map(CastSpellAction(player, _))
      .toSeq
  }

  private def cannotCastSpells(player: PlayerId, gameState: GameState): Boolean = {
    // TODO: effects like Epic
    false
  }

  private def cannotCast(player: PlayerId, gameState: GameState, objectWithState: ObjectWithState): Boolean = {
    if (objectWithState.characteristics.types.contains(Type.Land)) {
      // RULE 305.9 / Apr 22 2021 : If an object is both a land and another card type, it can be played only as a land.
      // It can't be cast as a spell.
      true
    } else {
      // TODO: effects like Grafdigger's Cage
      false
    }
  }

  private def hasGeneralPermissionToCastSpell(player: PlayerId, gameState: GameState, objectWithState: ObjectWithState): Boolean = {
    if (TypeChecks.hasSpellType(objectWithState) && objectWithState.gameObject.zone.zoneType == ZoneType.Hand) {
      true
    } else {
    // TODO: effects like Lurrus
      false
    }
  }

  private def hasTimingPermissionToCastSpell(player: PlayerId, gameState: GameState, objectWithState: ObjectWithState): Boolean = {
    if (TypeChecks.hasSpellType(objectWithState)) {
      objectWithState.characteristics.types.contains(Type.Instant) || TimingChecks.isMainPhaseOfPlayersTurnWithEmptyStack(player, gameState)
    } else {
      // TODO: effects like flash
      false
    }
  }
}
