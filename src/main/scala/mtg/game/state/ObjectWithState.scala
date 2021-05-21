package mtg.game.state

import mtg.abilities.AbilityDefinition
import mtg.game.PlayerIdentifier
import mtg.game.objects.GameObject

case class ObjectWithState(
    gameObject: GameObject,
    characteristics: Characteristics,
    controller: Option[PlayerIdentifier])
{
  def addAbility(abilityDefinition: AbilityDefinition): ObjectWithState = {
    copy(characteristics = characteristics.copy(abilities = characteristics.abilities :+ abilityDefinition))
  }
}

object ObjectWithState {
  def initial(gameObject: GameObject): ObjectWithState = ObjectWithState(gameObject, gameObject.baseCharacteristics, None)
}
