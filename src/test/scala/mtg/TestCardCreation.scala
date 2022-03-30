package mtg

import mtg.cards.CardDefinition
import mtg.cards.patterns.{CreatureCard, SpellCard}
import mtg.cards.text.TextParagraph
import mtg.core.types.Type
import mtg.parts.costs.ManaCost

import java.util.concurrent.atomic.AtomicInteger

trait TestCardCreation {
  val nextCardId = new AtomicInteger()

  def getNewCardName(): String = "Test Card " + nextCardId.getAndIncrement()

  def simpleCreature(
    textParagraphs: Seq[TextParagraph],
    powerAndToughness: (Int, Int)
  ): CardDefinition = new CreatureCard(getNewCardName(), ManaCost(0), Nil, textParagraphs, powerAndToughness)

  def vanillaCreature(power: Int, toughness: Int): CardDefinition = simpleCreature(Nil, (power, toughness))

  def simpleInstantSpell(textParagraphs: Seq[TextParagraph]): CardDefinition = new SpellCard(
    getNewCardName(),
    ManaCost(0),
    Type.Instant,
    Nil,
    textParagraphs)
}
