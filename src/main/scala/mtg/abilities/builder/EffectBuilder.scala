package mtg.abilities.builder

import mtg._
import mtg.cards.text.SpellEffectSentence
import mtg.effects.OneShotEffect
import mtg.effects.condition.ConditionDefinition
import mtg.effects.filters.Filter
import mtg.effects.identifiers.{FilterIdentifier, MultipleIdentifier, SingleIdentifier}
import mtg.effects.oneshot.CharacteristicOrControlChangingContinuousEffectCreationEffect
import mtg.effects.oneshot.actions._
import mtg.effects.oneshot.basic._
import mtg.effects.oneshot.descriptions.CharacteristicOrControlChangingContinuousEffectDescription
import mtg.game.{ObjectId, ObjectOrPlayer, PlayerId}
import mtg.parts.counters.CounterType

object EffectBuilder
  extends FilterBuilder
    with IdentifierBuilder
    with TargetBuilder
    with ConditionBuilder
    with NumberBuilder
    with ContinuousEffectBuilder
    with TriggeredAbilityBuilder
    with ParagraphBuilder
{

  abstract class EffectsSeqExtension(effects: Seq[OneShotEffect]) {
    def `then`(effect: OneShotEffect): SpellEffectSentence = SpellEffectSentence.MultiClause(effects :+ effect, "then")
  }
  implicit class EffectExtension(effect: OneShotEffect) extends EffectsSeqExtension(Seq(effect))
  implicit class ThreeEffectsExtension(effects: (OneShotEffect, OneShotEffect, OneShotEffect)) extends EffectsSeqExtension(effects.productIterator.toSeq.ofType[OneShotEffect])

  case class DealEffectBuilder(objectIdentifier: SingleIdentifier[ObjectId], amount: Int) {
      def damageTo(recipientIdentifier: SingleIdentifier[ObjectOrPlayer]): OneShotEffect = DealDamageEffect(objectIdentifier, recipientIdentifier, amount)
  }
  case class ContinuousEffectBuilder(objectIdentifier: MultipleIdentifier[ObjectId], continuousEffectDescriptions: Seq[CharacteristicOrControlChangingContinuousEffectDescription]) {
      def until(conditionDefinition: ConditionDefinition): OneShotEffect = CharacteristicOrControlChangingContinuousEffectCreationEffect(objectIdentifier, continuousEffectDescriptions, conditionDefinition)
  }

  implicit class ObjectSingleIdentifierExtension(objectIdentifier: SingleIdentifier[ObjectId]) {
    def deals(amount: Int): DealEffectBuilder = DealEffectBuilder(objectIdentifier, amount)
  }
  implicit class ObjectMultipleIdentifierExtension(objectIdentifier: MultipleIdentifier[ObjectId]) {
    def apply(continuousEffectDescriptions: CharacteristicOrControlChangingContinuousEffectDescription*): ContinuousEffectBuilder = ContinuousEffectBuilder(objectIdentifier, continuousEffectDescriptions)
  }

  implicit class ObjectFilterExtension(objectFilter: Filter[ObjectId]) extends ObjectMultipleIdentifierExtension(FilterIdentifier(objectFilter))

  implicit class PlayerIdentifierExtension(playerIdentifier: SingleIdentifier[PlayerId]) {
    def gain(amount: Int) = new {
      def life: OneShotEffect = GainLifeEffect(playerIdentifier, amount)
    }
    def drawsACard: OneShotEffect = DrawsACardEffect(playerIdentifier)
  }

  def searchYourLibraryForA(objectFilter: Filter[ObjectId]): OneShotEffect = SearchLibraryEffect(objectFilter)
  def reveal(objectIdentifier: SingleIdentifier[ObjectId]): OneShotEffect = RevealEffect(objectIdentifier)
  def put(objectIdentifier: SingleIdentifier[ObjectId]) = new {
    def intoYourHand: OneShotEffect = PutIntoHandEffect(objectIdentifier)
  }
  def destroy(objectIdentifier: SingleIdentifier[ObjectId]): OneShotEffect = DestroyEffect(objectIdentifier)
  def exile(objectIdentifier: SingleIdentifier[ObjectId]): OneShotEffect = ExileEffect(objectIdentifier)
  def put(number: Int, counterType: CounterType) = new {
    def on(objectIdentifier: SingleIdentifier[ObjectId]): OneShotEffect = PutCountersEffect(number, counterType, objectIdentifier)
  }
  def shuffle: OneShotEffect = ShuffleEffect
  def scry(number: Int): OneShotEffect = ScryEffect(number)
  def drawACard: OneShotEffect = DrawACardEffect
}
