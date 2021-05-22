package mtg.game.actions

import mtg.characteristics.types.Type
import mtg.game.{PlayerIdentifier, ZoneType}
import mtg.game.objects.ObjectId
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameState, ObjectWithState}

case class CastSpellAction(player: PlayerIdentifier, objectToCast: ObjectWithState) extends PriorityAction {
  override def objectId: ObjectId = objectToCast.gameObject.objectId
  override def displayText: String = "Cast"
  override def optionText: String = "Cast " + objectId

  override def execute(currentGameState: GameState): (Seq[GameAction], Option[LogEvent]) = {
    (Nil, None)
  }
}

object CastSpellAction {
  def getCastableSpells(player: PlayerIdentifier, gameState: GameState): Seq[CastSpellAction] = {
    if (cannotCastSpells(player, gameState)) {
      return Nil
    }
    gameState.derivedState.allObjectStates.view
      .filter(isSpell)
      .filter(!cannotCastSpell(player, gameState, _))
      .filter(hasGeneralPermissionToCastSpell(player, gameState, _))
      .filter(hasTimingPermissionToCastSpell(player, gameState, _))
      .map(CastSpellAction(player, _))
      .toSeq
  }

  private def isSpell(objectWithState: ObjectWithState): Boolean = {
    objectWithState.characteristics.types.exists(_.isInstanceOf[Type.SpellType])
  }

  private def cannotCastSpells(player: PlayerIdentifier, gameState: GameState): Boolean = {
    // TODO: effects like Epic
    false
  }

  private def cannotCastSpell(player: PlayerIdentifier, gameState: GameState, objectWithState: ObjectWithState): Boolean = {
    if (objectWithState.characteristics.types.contains(Type.Land)) {
      // RULE 305.9 / Apr 22 2021 : If an object is both a land and another card type, it can be played only as a land.
      // It can't be cast as a spell.
      true
    } else {
      // TODO: effects like Grafdigger's Cage
      false
    }
  }

  private def hasGeneralPermissionToCastSpell(player: PlayerIdentifier, gameState: GameState, objectWithState: ObjectWithState): Boolean = {
    if (objectWithState.gameObject.zone.zoneType == ZoneType.Hand) {
      true
    } else {
    // TODO: effects like Lurrus
      false
    }
  }

  private def hasTimingPermissionToCastSpell(player: PlayerIdentifier, gameState: GameState, objectWithState: ObjectWithState): Boolean = {
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
