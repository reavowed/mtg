package mtg.instructions.verbs

import mtg.core.{ObjectId, PlayerId}
import mtg.effects.EffectContext
import mtg.game.state.GameState
import mtg.instructions.{StateDescriptionVerb, Verb}

object Control extends Verb.RegularCaseObject with StateDescriptionVerb[PlayerId] {
  override def describes(subject: PlayerId, objectId: ObjectId, gameState: GameState, effectContext: EffectContext): Boolean = {
      gameState.gameObjectState.derivedState.stackObjectStates.get(objectId).map(_.controller)
        .orElse(gameState.gameObjectState.derivedState.permanentStates.get(objectId).map(_.controller))
        .contains(subject)
  }
}
