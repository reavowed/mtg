package mtg.instructions

import mtg.core.PlayerId
import mtg.effects.StackObjectResolutionContext
import mtg.effects.identifiers.SingleIdentifier
import mtg.game.state.GameState
import mtg.instructions.nouns.SingleIdentifyingNounPhrase
import mtg.text.{Verb, VerbInflection}

trait IntransitiveInstructionVerb extends Verb {
  def resolve(playerId: PlayerId, gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult

  def imperative: Instruction = IntransitiveInstructionVerb.Imperative(this)
}

object IntransitiveInstructionVerb {
  implicit def toInstruction(intransitiveVerbInstruction: IntransitiveInstructionVerb): Instruction = {
    intransitiveVerbInstruction.imperative
  }

  case class Imperative(instructionVerb: IntransitiveInstructionVerb) extends Instruction {
    override def getText(cardName: String): String = instructionVerb.inflect(VerbInflection.Imperative, cardName)
    override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
      instructionVerb.resolve(resolutionContext.controllingPlayer, gameState, resolutionContext)
    }
  }
  case class WithPlayer(playerNoun: SingleIdentifyingNounPhrase[PlayerId], instructionVerb: IntransitiveInstructionVerb) extends Instruction {
    override def getText(cardName: String): String = {
      playerNoun.getText(cardName) + " " + instructionVerb.inflect(VerbInflection.Present(playerNoun.person, playerNoun.number), cardName)
    }
    override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
      val (playerId, contextAfterPlayer) = playerNoun.identify(gameState, resolutionContext)
      instructionVerb.resolve(playerId, gameState, contextAfterPlayer)
    }
  }
}
