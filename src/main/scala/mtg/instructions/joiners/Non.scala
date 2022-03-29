package mtg.instructions.joiners

import mtg.instructions.adjectives.Adjective

object Non {
  def apply(adjective: Adjective): Adjective = Adjective.Non(adjective)
}
