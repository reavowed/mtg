package mtg.abilities.builder

import mtg.core.types.{Supertype, Type}
import mtg.core.{ObjectId, PlayerId}
import mtg.effects.filters.PartialFilter
import mtg.effects.filters.base._
import mtg.instructions.nounPhrases.StaticSingleIdentifyingNounPhrase

trait FilterBuilder {
  implicit class PlayerIdentifierExtensions(playerIdentifier: StaticSingleIdentifyingNounPhrase[PlayerId]) {
    def control: PartialFilter[ObjectId] = ControllerFilter(playerIdentifier)
  }

  implicit def typeToFilter(t: Type): PartialFilter[ObjectId] = TypeFilter(t)
  implicit def supertypeToFilter(supertype: Supertype): PartialFilter[ObjectId] = SupertypeFilter(supertype)
}

