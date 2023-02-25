package mtg.instructions.verbs

import mtg.actions.AddManaAction
import mtg.definitions.PlayerId
import mtg.definitions.symbols.ManaSymbol
import mtg.instructions.{InstructionAction, IntransitiveInstructionVerb, Verb}

case class Add(symbols: ManaSymbol*) extends Verb.WithSuffix(Verb.Add, symbols.map(_.text).mkString) with IntransitiveInstructionVerb[PlayerId] {
  override def resolve(playerId: PlayerId): InstructionAction = {
    AddManaAction(playerId, symbols)
  }
}
