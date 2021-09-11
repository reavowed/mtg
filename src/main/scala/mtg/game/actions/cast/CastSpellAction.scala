package mtg.game.actions.cast

import mtg.characteristics.types.Type
import mtg.events.MoveObjectEvent
import mtg.game.actions.{PriorityAction, TimingChecks}
import mtg.game.stack.steps
import mtg.game.stack.steps.FinishCasting
import mtg.game.state.{BackupAction, InternalGameActionResult, GameState, ObjectWithState}
import mtg.game.{ObjectId, PlayerId, Zone, ZoneType}

case class CastSpellAction(player: PlayerId, objectToCast: ObjectWithState, backupAction: BackupAction) extends PriorityAction {
  override def objectId: ObjectId = objectToCast.gameObject.objectId
  override def displayText: String = "Cast"
  override def optionText: String = "Cast " + objectId

  override def execute(currentGameState: GameState): InternalGameActionResult = {
    Seq(
      MoveObjectEvent(player, objectId, Zone.Stack),
      steps.CastSpellAndActivateAbilitySteps(FinishCasting, backupAction))
  }
}

object CastSpellAction {
  def getCastableSpells(player: PlayerId, gameState: GameState, backupAction: BackupAction): Seq[CastSpellAction] = {
    if (cannotCastSpells(player, gameState)) {
      return Nil
    }
    gameState.gameObjectState.derivedState.allObjectStates.values.view
      .filter(isSpell)
      .filter(!cannotCastSpell(player, gameState, _))
      .filter(hasGeneralPermissionToCastSpell(player, gameState, _))
      .filter(hasTimingPermissionToCastSpell(player, gameState, _))
      .map(CastSpellAction(player, _, backupAction))
      .toSeq
  }

  private def isSpell(objectWithState: ObjectWithState): Boolean = {
    objectWithState.characteristics.types.exists(_.isSpell)
  }

  private def cannotCastSpells(player: PlayerId, gameState: GameState): Boolean = {
    // TODO: effects like Epic
    false
  }

  private def cannotCastSpell(player: PlayerId, gameState: GameState, objectWithState: ObjectWithState): Boolean = {
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
    if (objectWithState.gameObject.zone.zoneType == ZoneType.Hand) {
      true
    } else {
    // TODO: effects like Lurrus
      false
    }
  }

  private def hasTimingPermissionToCastSpell(player: PlayerId, gameState: GameState, objectWithState: ObjectWithState): Boolean = {
    if (objectWithState.characteristics.types.contains(Type.Instant)) {
      // RULE 117.1a / Apr 22 2021: A player may cast an instant spell any time they have priority.
      true
    } else {
      // RULE 117.1a / Apr 22 2021: A player may cast a noninstant spell during their main phase any time they have
      // priority and the stack is empty.
      TimingChecks.isMainPhaseOfPlayersTurnWithEmptyStack(player, gameState)
    }
  }
}
