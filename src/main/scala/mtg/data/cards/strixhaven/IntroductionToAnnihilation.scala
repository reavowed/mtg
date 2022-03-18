package mtg.data.cards.strixhaven

import mtg.abilities.builder.InstructionBuilder._
import mtg.cards.patterns.SpellCard
import mtg.cards.text.SimpleInstructionParagraph
import mtg.core.types.SpellType.Lesson
import mtg.core.types.Type
import mtg.core.types.Type.Land
import mtg.instructions.actions.{DrawACard, Exile}
import mtg.parts.costs.ManaCost

object IntroductionToAnnihilation extends SpellCard(
  "Introduction to Annihilation",
  ManaCost(5),
  Type.Sorcery,
  Seq(Lesson),
  SimpleInstructionParagraph(
    Exile(target(permanent(non(Land)))),
    DrawACard(it.s(controller))))
