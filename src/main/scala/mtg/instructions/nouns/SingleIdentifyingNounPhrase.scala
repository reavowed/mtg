package mtg.instructions.nouns

import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.instructions.{Instruction, IntransitiveInstructionVerb, TextComponent}
import mtg.text.{VerbNumber, VerbPerson}

trait SingleIdentifyingNounPhrase[T] extends TextComponent {
  def identify(gameState: GameState, resolutionContext: StackObjectResolutionContext): (T, StackObjectResolutionContext)
  def getPossessiveText(cardName: String): String = getText(cardName) + "'s"
  def person: VerbPerson
  def number: VerbNumber

  def apply(verb: IntransitiveInstructionVerb[T]): Instruction = {
    IntransitiveInstructionVerb.WithSubject(this, verb)
  }
}
