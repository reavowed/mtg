package mtg.sets.strixhaven.cards

import mtg.abilities.builder.TypeConversions._
import mtg.cards.patterns.SpellCard
import mtg.cards.text.SimpleInstructionParagraph
import mtg.core.types.SpellType.Lesson
import mtg.core.types.Type
import mtg.core.types.Type.Land
import mtg.instructions.joiners.Non
import mtg.instructions.nounPhrases.{Controller, It, Target}
import mtg.instructions.nouns.Permanent
import mtg.instructions.verbs.{DrawACard, Exile}
import mtg.parts.costs.ManaCost

object IntroductionToAnnihilation extends SpellCard(
  "Introduction to Annihilation",
  ManaCost(5),
  Type.Sorcery,
  Seq(Lesson),
  SimpleInstructionParagraph(
    Exile(Target(Non(Land)(Permanent))),
    Controller(It)(DrawACard)))
