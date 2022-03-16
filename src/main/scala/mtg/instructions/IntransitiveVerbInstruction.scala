package mtg.instructions

import mtg.core.PlayerId
import mtg.effects.StackObjectResolutionContext
import mtg.effects.identifiers.SingleIdentifier
import mtg.game.state.GameState
import mtg.text.{Verb, VerbInflection}

trait IntransitiveVerbInstruction extends Verb {
  def resolve(playerId: PlayerId, gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult

  def imperative: Instruction = IntransitiveVerbInstruction.Imperative(this)
  def apply(playerIdentifier: SingleIdentifier[PlayerId]): Instruction = {
    IntransitiveVerbInstruction.WithPlayer(playerIdentifier, this)
  }
}

object IntransitiveVerbInstruction {
  implicit def toInstruction(intransitiveVerbInstruction: IntransitiveVerbInstruction): Instruction = {
    intransitiveVerbInstruction.imperative
  }

  case class Imperative(intransitiveVerbInstruction: IntransitiveVerbInstruction) extends Instruction {
    override def getText(cardName: String): String = intransitiveVerbInstruction.inflect(VerbInflection.Imperative, cardName)
    override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
      intransitiveVerbInstruction.resolve(resolutionContext.controllingPlayer, gameState, resolutionContext)
    }
  }
  case class WithPlayer(playerIdentifier: SingleIdentifier[PlayerId], intransitiveVerbInstruction: IntransitiveVerbInstruction) extends Instruction {
    override def getText(cardName: String): String = {
      playerIdentifier.getText(cardName) + " " + intransitiveVerbInstruction.inflect(VerbInflection.Present(playerIdentifier.person, playerIdentifier.number), cardName)
    }
    override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
      val (playerId, contextAfterPlayer) = playerIdentifier.get(gameState, resolutionContext)
      intransitiveVerbInstruction.resolve(playerId, gameState, contextAfterPlayer)
    }
  }
}
