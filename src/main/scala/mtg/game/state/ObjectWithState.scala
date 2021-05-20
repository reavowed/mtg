package mtg.game.state

import mtg.game.PlayerIdentifier
import mtg.game.objects.GameObject

case class ObjectWithState(
  gameObject: GameObject,
  characteristics: Characteristics,
  controller: Option[PlayerIdentifier])

object ObjectWithState {
  def initial(gameObject: GameObject): ObjectWithState = ObjectWithState(gameObject, gameObject.baseCharacteristics, None)
}
