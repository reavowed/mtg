package mtg.instructions.conditions

import mtg.abilities.TriggeredAbilityDefinition
import mtg.cards.text.InstructionParagraph
import mtg.core.{ObjectId, PlayerId}
import mtg.effects.EffectContext
import mtg.effects.condition.Condition
import mtg.game.state.{GameState, GameUpdate}
import mtg.instructions.{Instruction, IntransitiveEventMatchingVerb, TransitiveEventMatchingVerb}
import mtg.instructions.nouns.IndefiniteNounPhrase

case class When(playerPhrase: IndefiniteNounPhrase[PlayerId], verb: IntransitiveEventMatchingVerb) extends Condition {
  def getText(cardName: String): String = "when " + playerPhrase.getText(cardName)
  override def matchesEvent(eventToMatch: GameUpdate, gameState: GameState, effectContext: EffectContext): Boolean = {
    verb.matchesEvent(eventToMatch, gameState, effectContext, playerPhrase)
  }

  def apply(instructionParagraph: InstructionParagraph): TriggeredAbilityDefinition = {
    TriggeredAbilityDefinition(this, instructionParagraph)
  }
}
object When {
  def apply(
    playerPhrase: IndefiniteNounPhrase[PlayerId],
    verb: TransitiveEventMatchingVerb,
    objectPhrase: IndefiniteNounPhrase[ObjectId]
  ): When = {
    When(playerPhrase, verb(objectPhrase))
  }
}
