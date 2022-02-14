package mtg.events

import mtg.game.objects.{BasicGameObject, GameObject, PermanentObject, StackObject}
import mtg.game.state.{GameActionResult, GameState, InternalGameAction, StackObjectWithState}
import mtg.game.{ObjectId, PlayerId, Zone}

case class MoveObjectEvent(player: PlayerId, objectId: ObjectId, destination: Zone) extends InternalGameAction {
  def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.derivedState.allObjectStates.get(objectId).map(existingObjectWithState => {
        // TODO: Handle putting something onto the battlefield under another player's control
      val gameObjectStateWithoutExistingObject = gameState.gameObjectState
        .deleteObject(existingObjectWithState.gameObject)
      destination match {
          case Zone.Battlefield =>
            val controller = existingObjectWithState.asOptionalInstanceOf[StackObjectWithState].map(_.controller).getOrElse(player)
            gameObjectStateWithoutExistingObject
              .createObject(newObjectId => PermanentObject(existingObjectWithState.gameObject.underlyingObject, newObjectId, controller), _.length)
          case Zone.Stack =>
            gameObjectStateWithoutExistingObject
                .createObject(newObjectId => StackObject(existingObjectWithState.gameObject.underlyingObject, newObjectId, player), _.length)
          case destination: Zone.BasicZone =>
            gameObjectStateWithoutExistingObject
                .createObject(newObjectId => BasicGameObject(existingObjectWithState.gameObject.underlyingObject, newObjectId, destination), _.length)
      }
    })
  }
  // TODO: figure out how to make this revertible if the previous card was not hidden
  override def canBeReverted: Boolean = false
}

object MoveObjectEvent {
  def apply(player: PlayerId, gameObject: GameObject, destination: Zone): MoveObjectEvent = {
    MoveObjectEvent(player, gameObject.objectId, destination)
  }
}
