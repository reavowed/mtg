package mtg

import mtg.abilities.TriggeredAbilityDefinition
import mtg.cards.CardDefinition
import mtg.cards.patterns.{ArtifactCard, CreatureCard, SpellCard}
import mtg.cards.text.TextParagraph
import mtg.definitions.types.{CreatureType, SpellType, Type}
import mtg.parts.costs.ManaCost

import java.util.concurrent.atomic.AtomicInteger

trait TestCardCreation {
  val nextCardId = new AtomicInteger()

  def getNewCardName(): String = "Test Card " + nextCardId.getAndIncrement()

  def creature(
    manaCost: ManaCost,
    types: Seq[CreatureType],
    textParagraphs: Seq[TextParagraph],
    powerAndToughness: (Int, Int)
  ): CardDefinition = new CreatureCard(getNewCardName(), manaCost, types, textParagraphs, powerAndToughness)

  def zeroManaCreature(
    textParagraphs: Seq[TextParagraph],
    powerAndToughness: (Int, Int)
  ): CardDefinition = new CreatureCard(getNewCardName(), ManaCost(0), Nil, textParagraphs, powerAndToughness)

  def vanillaCreature(manaCost: ManaCost, powerAndToughness: (Int, Int)): CardDefinition = creature(manaCost, Nil, Nil, powerAndToughness)
  def vanillaCreature(power: Int, toughness: Int): CardDefinition = zeroManaCreature(Nil, (power, toughness))

  def simpleSpell(t: Type.InstantOrSorcery, subtypes: Seq[SpellType], textParagraphs: Seq[TextParagraph]): SpellCard = new SpellCard(
    getNewCardName(),
    ManaCost(0),
    t,
    subtypes,
    textParagraphs)
  def simpleSpell(t: Type.InstantOrSorcery, textParagraphs: Seq[TextParagraph]): SpellCard = simpleSpell(t, Nil, textParagraphs)
  def simpleInstantSpell(textParagraphs: Seq[TextParagraph]): CardDefinition = simpleSpell(Type.Instant, textParagraphs)
  def simpleSorcerySpell(textParagraphs: Seq[TextParagraph]): CardDefinition = simpleSpell(Type.Sorcery, textParagraphs)
  def simpleSorcerySpell(subtypes: Seq[SpellType], textParagraphs: Seq[TextParagraph]): CardDefinition = simpleSpell(Type.Sorcery, subtypes, textParagraphs)

  def artifactWithTrigger(triggeredAbilityDefinition: TriggeredAbilityDefinition): CardDefinition = new ArtifactCard(
    getNewCardName(),
    ManaCost(0),
    triggeredAbilityDefinition)
}
