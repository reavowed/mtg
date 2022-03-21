package mtg.instructions.nouns

import mtg.core.PlayerId
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.instructions.{Instruction, IntransitiveInstructionVerb, TextComponent}
import mtg.text.{VerbNumber, VerbPerson}

trait SingleIdentifyingNounPhrase[T] extends TextComponent {
  def identify(gameState: GameState, resolutionContext: StackObjectResolutionContext): (T, StackObjectResolutionContext)
  def getPossessiveText(cardName: String): String = getText(cardName) + "'s"
  def person: VerbPerson
  def number: VerbNumber
}

object SingleIdentifyingNounPhrase {
  implicit class PlayerNounExtensions(playerNoun: SingleIdentifyingNounPhrase[PlayerId]) {
    def apply(intransitiveInstructionVerb: IntransitiveInstructionVerb): Instruction = {
      IntransitiveInstructionVerb.WithPlayer(playerNoun, intransitiveInstructionVerb)
    }
  }
}
