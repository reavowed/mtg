package mtg.sbas

import mtg.characteristics.types.Type
import mtg.events.DestroyAction
import mtg.game.state.{GameObjectAction, GameState}

object LethalDamageStateBasedAction {
  def getApplicableEvents(gameState: GameState): Seq[GameObjectAction] = {
    // RULE 704.5g. If a creature has toughness greater than 0, it has damage marked on it, and the total damage marked
    // on it is greater than or equal to its toughness, that creature has been dealt lethal damage and is destroyed.
    // Regeneration can replace this event.
    gameState.gameObjectState.battlefield.view
      .filter(gameObject => {
        val characteristics = gameObject.currentCharacteristics(gameState)
        characteristics.types.contains(Type.Creature) &&
          gameObject.markedDamage > 0 &&
          gameObject.markedDamage >= characteristics.toughness.getOrElse(0)
      })
      .map(gameObject => DestroyAction(gameObject.owner, gameObject.objectId))
      .toSeq
  }
}
