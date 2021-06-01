package mtg.abilities.builder

import mtg._
import mtg.abilities.AbilityDefinition
import mtg.cards.text.SpellEffectSentence
import mtg.effects.OneShotEffect
import mtg.effects.condition.ConditionDefinition
import mtg.effects.filters.Filter
import mtg.effects.identifiers.Identifier
import mtg.effects.oneshot.basic.{GainAbilityEffect, _}
import mtg.effects.oneshot.basic
import mtg.game.{ObjectId, ObjectOrPlayer, PlayerId}
import mtg.parts.counters.CounterType

object EffectBuilder extends FilterBuilder with IdentifierBuilder with TargetBuilder with ConditionBuilder {

  abstract class EffectsSeqExtension(effects: Seq[OneShotEffect]) {
    def `then`(effect: OneShotEffect): SpellEffectSentence = SpellEffectSentence.MultiClause(effects :+ effect, "then")
  }

  implicit class ThreeEffectsExtension(effects: (OneShotEffect, OneShotEffect, OneShotEffect)) extends EffectsSeqExtension(effects.productIterator.toSeq.ofType[OneShotEffect])

  case class DealEffectBuilder(objectIdentifier: Identifier[ObjectId], amount: Int) {
      def damageTo(recipientIdentifier: Identifier[ObjectOrPlayer]): DealDamageEffect = DealDamageEffect(objectIdentifier, recipientIdentifier, amount)
  }
  case class GainAbilityEffectBuilder(objectIdentifier: Identifier[ObjectId], abilityDefinition: AbilityDefinition) {
      def until(conditionDefinition: ConditionDefinition): GainAbilityEffect = basic.GainAbilityEffect(objectIdentifier, abilityDefinition, conditionDefinition)
  }

  implicit class ObjectIdentifierExtension(objectIdentifier: Identifier[ObjectId]) {
    def deals(amount: Int) = DealEffectBuilder(objectIdentifier, amount)
    def gains(abilityDefinition: AbilityDefinition) = GainAbilityEffectBuilder(objectIdentifier, abilityDefinition)
  }

  implicit class PlayerIdentifierExtension(playerIdentifier: Identifier[PlayerId]) {
    def gain(amount: Int) = new {
      def life = GainLifeEffect(playerIdentifier, amount)
    }
    def drawsACard = new DrawACardEffect(playerIdentifier)
  }

  def searchYourLibraryForA(objectFilter: Filter[ObjectId]): SearchLibraryEffect = SearchLibraryEffect(objectFilter)
  def reveal(objectIdentifier: Identifier[ObjectId]): RevealEffect = RevealEffect(objectIdentifier)
  def put(objectIdentifier: Identifier[ObjectId]) = new {
    def intoYourHand = basic.PutIntoHandEffect(objectIdentifier)
  }
  def exile(objectIdentifier: Identifier[ObjectId]): ExileEffect = ExileEffect(objectIdentifier)
  def put(number: Int, counterType: CounterType) = new {
    def on(objectIdentifier: Identifier[ObjectId]): PutCountersEffect = PutCountersEffect(number, counterType, objectIdentifier)
  }
  def shuffle: ShuffleEffect.type = ShuffleEffect
}
