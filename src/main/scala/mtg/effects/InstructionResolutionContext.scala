package mtg.effects

import mtg.abilities.{ManaAbility, TriggeredAbility}
import mtg.core.{ObjectId, ObjectOrPlayerId, PlayerId}
import mtg.game.objects.{AbilityOnTheStack, Card, CopyOfSpell}
import mtg.game.state.{CurrentCharacteristics, GameState, StackObjectWithState}

case class InstructionResolutionContext(
    override val cardNameObjectId: ObjectId,
    override val thisObjectId: ObjectId,
    override val youPlayerId: PlayerId,
    identifiedObjects: Seq[ObjectOrPlayerId],
    targets: Seq[ObjectOrPlayerId])
  extends EffectContext(cardNameObjectId, thisObjectId, youPlayerId)
{
  def addIdentifiedObject(objectId: ObjectOrPlayerId): InstructionResolutionContext = copy(identifiedObjects = identifiedObjects :+ objectId)
  def popTarget: (ObjectOrPlayerId, InstructionResolutionContext) = {
    val target = targets.head
    (target, addIdentifiedObject(target).copy(targets = targets.tail))
  }
  def cardName(implicit gameState: GameState): String = {
    CurrentCharacteristics.getName(gameState.gameObjectState.getCurrentOrLastKnownState(cardNameObjectId))
  }
}
object InstructionResolutionContext {
  def forSpellOrAbility(spellWithState: StackObjectWithState): InstructionResolutionContext = {
    InstructionResolutionContext(
      spellWithState.cardNameObjectId,
      spellWithState.thisObjectId,
      spellWithState.controller,
      spellWithState.gameObject.underlyingObject.asOptionalInstanceOf[AbilityOnTheStack].map(_.identifiedObjects).getOrElse(Nil),
      spellWithState.gameObject.targets)
  }
  def forManaAbility(manaAbility: ManaAbility): InstructionResolutionContext = {
    InstructionResolutionContext(
      manaAbility.sourceId,
      manaAbility.sourceId,
      manaAbility.controllingPlayer,
      Nil,
      Nil)
  }
  def forTriggeredAbility(triggeredAbility: TriggeredAbility): InstructionResolutionContext = {
    InstructionResolutionContext(
      triggeredAbility.sourceId,
      triggeredAbility.sourceId,
      triggeredAbility.controllerId,
      Nil,
      Nil)
  }
}
