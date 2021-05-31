package mtg.abilities.builder

import mtg._
import mtg.cards.text.SpellEffectSentence
import mtg.effects.filters.Filter
import mtg.effects.identifiers.Identifier
import mtg.effects.oneshot.basic._
import mtg.effects.oneshot.{OneShotEffect, basic}
import mtg.game.{ObjectId, ObjectOrPlayer, PlayerId}
import mtg.parts.counters.CounterType

object EffectBuilder extends FilterBuilder with IdentifierBuilder with TargetBuilder {

  abstract class EffectsSeqExtension(effects: Seq[OneShotEffect]) {
    def `then`(effect: OneShotEffect): SpellEffectSentence = SpellEffectSentence.MultiClause(effects :+ effect, "then")
  }

  implicit class ThreeEffectsExtension(effects: (OneShotEffect, OneShotEffect, OneShotEffect)) extends EffectsSeqExtension(effects.productIterator.toSeq.ofType[OneShotEffect])

  implicit class ObjectIdentifierExtension(objectIdentifier: Identifier[ObjectId]) {
    def deals(amount: Int) = new {
      def damageTo(recipientIdentifier: Identifier[ObjectOrPlayer]) = DealDamageEffect(objectIdentifier, recipientIdentifier, amount)
    }
  }

  implicit class PlayerIdentifierExtension(playerIdentifier: Identifier[PlayerId]) {
    def gain(amount: Int) = new {
      def life = GainLifeEffect(playerIdentifier, amount)
    }
  }

  def searchYourLibraryForA(objectFilter: Filter[ObjectId]): SearchLibraryEffect = SearchLibraryEffect(objectFilter)
  def reveal(objectIdentifier: Identifier[ObjectId]): RevealEffect = RevealEffect(objectIdentifier)
  def put(objectIdentifier: Identifier[ObjectId]) = new {
    def intoYourHand = basic.PutIntoHandEffect(objectIdentifier)
  }
  def put(number: Int, counterType: CounterType) = new {
    def on(objectIdentifier: Identifier[ObjectId]): PutCountersEffect = PutCountersEffect(number, counterType, objectIdentifier)
  }
  def shuffle: ShuffleEffect.type = ShuffleEffect
}
