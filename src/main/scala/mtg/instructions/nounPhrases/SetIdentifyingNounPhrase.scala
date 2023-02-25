package mtg.instructions.nounPhrases

import mtg.instructions.InstructionAction

trait SetIdentifyingNounPhrase[+T] extends NounPhrase {
  def identifyAll: InstructionAction.WithResult[Seq[T]]
  def getPossessiveText(cardName: String): String = getText(cardName) + "'s"
}
