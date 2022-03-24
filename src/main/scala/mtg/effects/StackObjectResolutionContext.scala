package mtg.effects

import mtg.abilities.ManaAbility
import mtg.core.{ObjectId, ObjectOrPlayerId, PlayerId}
import mtg.game.state.{GameState, StackObjectWithState}

case class StackObjectResolutionContext(
    override val sourceId: ObjectId,
    override val controllingPlayer: PlayerId,
    identifiedObjects: Seq[ObjectOrPlayerId],
    targets: Seq[ObjectOrPlayerId])
  extends EffectContext(sourceId, controllingPlayer)
{
  def addIdentifiedObject(objectId: ObjectOrPlayerId): StackObjectResolutionContext = copy(identifiedObjects = identifiedObjects :+ objectId)
  def popTarget: (ObjectOrPlayerId, StackObjectResolutionContext) = {
    val target = targets.head
    (target, addIdentifiedObject(target).copy(targets = targets.tail))
  }
}
object StackObjectResolutionContext {
  def forSpellOrAbility(spellWithState: StackObjectWithState, gameState: GameState): StackObjectResolutionContext = {
    StackObjectResolutionContext(
      spellWithState.gameObject.objectId,
      spellWithState.controller,
      Nil,
      spellWithState.gameObject.targets)
  }
  def forManaAbility(manaAbility: ManaAbility, gameState: GameState): StackObjectResolutionContext = {
    StackObjectResolutionContext(
      manaAbility.sourceId,
      manaAbility.controllingPlayer,
      Nil,
      Nil)
  }
}
