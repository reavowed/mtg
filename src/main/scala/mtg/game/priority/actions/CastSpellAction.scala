package mtg.game.priority.actions

import mtg.characteristics.types.Type
import mtg.core.{ObjectId, PlayerId}
import mtg.core.zones.ZoneType
import mtg.events.moveZone.MoveToStackEvent
import mtg.game.state.{GameState, ObjectWithState, PartialGameActionResult, WrappedOldUpdates}
import mtg.stack.adding._

case class CastSpellAction(player: PlayerId, objectToCast: ObjectWithState) extends PriorityAction {
  override def objectId: ObjectId = objectToCast.gameObject.objectId
  override def displayText: String = "Cast"
  override def optionText: String = "Cast " + objectId

  override def execute()(implicit gameState: GameState): PartialGameActionResult[Any] = {
    PartialGameActionResult.ChildWithCallback(
      WrappedOldUpdates(MoveToStackEvent(objectId, player)),
      steps)
  }
  private def steps(any: Any, gameState: GameState): PartialGameActionResult[Any] = {
    // TODO: Should be result of MoveObjectEvent
    val spellId = gameState.gameObjectState.stack.last.objectId
    PartialGameActionResult.children(
      ChooseModes(spellId),
      ChooseTargets(spellId),
      PayManaCosts.ForSpell(spellId),
      FinishCasting(spellId))
  }
}

object CastSpellAction {
  def getCastableSpells(player: PlayerId, gameState: GameState): Seq[CastSpellAction] = {
    if (cannotCastSpells(player, gameState)) {
      return Nil
    }
    gameState.gameObjectState.derivedState.allObjectStates.values.view
      .filter(isSpell)
      .filter(!cannotCastSpell(player, gameState, _))
      .filter(hasGeneralPermissionToCastSpell(player, gameState, _))
      .filter(hasTimingPermissionToCastSpell(player, gameState, _))
      .map(CastSpellAction(player, _))
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
