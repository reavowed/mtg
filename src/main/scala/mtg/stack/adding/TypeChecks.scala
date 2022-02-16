package mtg.stack.adding

import mtg.characteristics.types.Type
import mtg.game.state.ObjectWithState

object TypeChecks {
  val PermanentSpellTypes = List(Type.Artifact, Type.Creature, Type.Enchantment, Type.Planeswalker)
  val PermanentTypes = PermanentSpellTypes :+ Type.Land
  val SpellTypes = PermanentSpellTypes ++ Seq(Type.Instant, Type.Sorcery)

  def hasPermanentType(objectWithState: ObjectWithState): Boolean = {
    hasAnyType(objectWithState, PermanentSpellTypes)
  }
  def hasSpellType(objectWithState: ObjectWithState): Boolean = {
    hasAnyType(objectWithState, SpellTypes)
  }

  private def hasAnyType(objectWithState: ObjectWithState, types: Seq[Type]): Boolean = {
    objectWithState.characteristics.types.exists(types.contains)
  }
}
