package mtg.data.cards.strixhaven

import mtg.cards.patterns.VanillaCreature
import mtg.characteristics.Color.White
import mtg.characteristics.types.CreatureType._
import mtg.parts.costs.ManaCost

object AgelessGuardian extends VanillaCreature(
  "Ageless Guardian",
  ManaCost(1, White),
  Seq(Spirit, Soldier),
  (1, 4))
