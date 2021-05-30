package mtg.abilities

import mtg._
import mtg.cards.text.SpellEffectSentence
import mtg.characteristics.types.Supertype.Basic
import mtg.characteristics.types.Type.Land
import mtg.effects.filters.{CompoundCharacteristicFilter, Filter, SupertypeFilter, TypeFilter}
import mtg.effects.identifiers.{Identifier, ItIdentifier, ThisIdentifier, YouIdentifier}
import mtg.effects.oneshot.basic.{DealDamageEffect, GainLifeEffect, PutIntoHandEffect, RevealEffect, SearchLibraryEffect, ShuffleEffect}
import mtg.effects.oneshot.{OneShotEffect, basic}
import mtg.effects.targets.AnyTarget
import mtg.game.{ObjectId, ObjectOrPlayer, PlayerId}

object EffectBuilder {
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
  def basicLand: Filter[ObjectId] = CompoundCharacteristicFilter(SupertypeFilter(Basic), TypeFilter(Land))
  def reveal(objectIdentifier: Identifier[ObjectId]): RevealEffect = RevealEffect(objectIdentifier)
  def it: Identifier[ObjectId] = ItIdentifier
  def `this`: Identifier[ObjectId] = ThisIdentifier
  def put(objectIdentifier: Identifier[ObjectId]) = new {
    def intoYourHand = basic.PutIntoHandEffect(objectIdentifier)
  }
  def putIntoYourHand(objectIdentifier: Identifier[ObjectId]): PutIntoHandEffect = PutIntoHandEffect(objectIdentifier)
  def shuffle: ShuffleEffect.type = ShuffleEffect
  def you: Identifier[PlayerId] = YouIdentifier
  def anyTarget: Identifier[ObjectOrPlayer] = AnyTarget
}
