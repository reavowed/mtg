package mtg.instructions.joiners

import mtg.continuousEffects.CharacteristicOrControlChangingContinuousEffect
import mtg.core.ObjectId
import mtg.instructions.CharacteristicChangingVerb
import mtg.instructions.grammar.VerbInflection
import mtg.utils.TextUtils._

object And {
  def apply(characteristicChangingVerbs: CharacteristicChangingVerb*): CharacteristicChangingVerb = new CharacteristicChangingVerb {
    override def inflect(verbInflection: VerbInflection, cardName: String): String = {
      characteristicChangingVerbs.map(_.inflect(verbInflection, cardName)).toCommaList("and")
    }
    override def getEffects(objectId: ObjectId): Seq[CharacteristicOrControlChangingContinuousEffect] = {
      characteristicChangingVerbs.flatMap(_.getEffects(objectId))
    }
  }
}
