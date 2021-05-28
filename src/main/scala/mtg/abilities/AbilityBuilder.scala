package mtg.abilities

import mtg.effects.{CardIdentifier, Effect, GainLifeEffect, ObjectFilter, PutIntoHandEffect, RevealEffect, SearchLibraryEffect, ShuffleEffect}

object AbilityBuilder {
  implicit class EffectsSeqExtension(effects: Seq[Effect]) {
    def then(effect: Effect): AbilitySentence = AbilitySentence.MultiClause(effects :+ effect, "then")
  }

  def searchYourLibraryForA(objectFilter: ObjectFilter): SearchLibraryEffect = SearchLibraryEffect(objectFilter)
  def basicLand: ObjectFilter = ObjectFilter.basicLand
  def reveal(cardIdentifier: CardIdentifier): RevealEffect = RevealEffect(cardIdentifier)
  def it: CardIdentifier = CardIdentifier.It
  def putIntoYourHand(cardIdentifier: CardIdentifier): PutIntoHandEffect = PutIntoHandEffect(cardIdentifier)
  def shuffle: ShuffleEffect.type = ShuffleEffect
  def youGainLife(amount: Int): GainLifeEffect = GainLifeEffect(amount)
}
