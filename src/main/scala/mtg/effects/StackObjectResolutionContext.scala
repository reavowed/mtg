package mtg.effects

import mtg.abilities.ManaAbility
import mtg.core.{ObjectId, ObjectOrPlayerId, PlayerId}
import mtg.game.state.{GameState, StackObjectWithState}

case class StackObjectResolutionContext(
    sourceId: ObjectId,
    override val sourceName: String,
    override val controllingPlayer: PlayerId,
    identifiedObjects: Seq[ObjectOrPlayerId],
    targets: Seq[ObjectOrPlayerId])
  extends EffectContext(controllingPlayer, sourceName)
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
      spellWithState.gameObject.underlyingObject.getSourceName(gameState),
      spellWithState.controller,
      Nil,
      spellWithState.gameObject.targets)
  }
  def forManaAbility(manaAbility: ManaAbility, gameState: GameState): StackObjectResolutionContext = {
    StackObjectResolutionContext(
      manaAbility.sourceId,
      gameState.gameObjectState.getCurrentOrLastKnownState(manaAbility.sourceId).gameObject.underlyingObject.getSourceName(gameState),
      manaAbility.controllingPlayer,
      Nil,
      Nil)
  }
}
