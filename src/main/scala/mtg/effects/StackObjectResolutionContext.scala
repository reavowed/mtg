package mtg.effects

import mtg.abilities.ManaAbility
import mtg.core.{ObjectId, ObjectOrPlayerId, PlayerId}
import mtg.game.objects.{AbilityOnTheStack, Card, CopyOfSpell}
import mtg.game.state.{CurrentCharacteristics, GameState, StackObjectWithState}

case class StackObjectResolutionContext(
    override val cardNameObjectId: ObjectId,
    override val thisObjectId: ObjectId,
    override val controllingPlayer: PlayerId,
    identifiedObjects: Seq[ObjectOrPlayerId],
    targets: Seq[ObjectOrPlayerId])
  extends EffectContext(cardNameObjectId, thisObjectId, controllingPlayer)
{
  def addIdentifiedObject(objectId: ObjectOrPlayerId): StackObjectResolutionContext = copy(identifiedObjects = identifiedObjects :+ objectId)
  def popTarget: (ObjectOrPlayerId, StackObjectResolutionContext) = {
    val target = targets.head
    (target, addIdentifiedObject(target).copy(targets = targets.tail))
  }
  def cardName(implicit gameState: GameState): String = {
    CurrentCharacteristics.getName(gameState.gameObjectState.getCurrentOrLastKnownState(cardNameObjectId))
  }
}
object StackObjectResolutionContext {
  def forSpellOrAbility(spellWithState: StackObjectWithState): StackObjectResolutionContext = {
    StackObjectResolutionContext(
      spellWithState.cardNameObjectId,
      spellWithState.thisObjectId,
      spellWithState.controller,
      Nil,
      spellWithState.gameObject.targets)
  }
  def forManaAbility(manaAbility: ManaAbility): StackObjectResolutionContext = {
    StackObjectResolutionContext(
      manaAbility.sourceId,
      manaAbility.sourceId,
      manaAbility.controllingPlayer,
      Nil,
      Nil)
  }
}
