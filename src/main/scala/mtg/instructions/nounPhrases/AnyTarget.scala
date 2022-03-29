package mtg.instructions.nounPhrases

import mtg.core.types.Type
import mtg.instructions.TypePhrase
import mtg.instructions.joiners.Or
import mtg.instructions.nouns.Player

object AnyTarget extends Target(Or.apply(
  TypePhrase(Type.Creature),
  TypePhrase(Type.Planeswalker),
  Player)
) {
  override def getText(cardName: String): String = "any target"
}
