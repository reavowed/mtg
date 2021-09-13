package mtg.abilities.builder

import mtg._
import mtg.cards.text.SpellEffectSentence
import mtg.effects.OneShotEffect
import mtg.effects.condition.ConditionDefinition
import mtg.effects.filters.Filter
import mtg.effects.identifiers.Identifier
import mtg.effects.oneshot.ContinuousEffectCreationEffect
import mtg.effects.oneshot.actions._
import mtg.effects.oneshot.basic._
import mtg.effects.oneshot.descriptions.ContinuousEffectDescription
import mtg.game.{ObjectId, ObjectOrPlayer, PlayerId}
import mtg.parts.counters.CounterType

object EffectBuilder
  extends FilterBuilder
    with IdentifierBuilder
    with TargetBuilder
    with ConditionBuilder
    with ContinuousEffectBuilder
    with TriggeredAbilityBuilder
    with ParagraphBuilder
{

  abstract class EffectsSeqExtension(effects: Seq[OneShotEffect]) {
    def `then`(effect: OneShotEffect): SpellEffectSentence = SpellEffectSentence.MultiClause(effects :+ effect, "then")
  }
  implicit class EffectExtension(effect: OneShotEffect) extends EffectsSeqExtension(Seq(effect))
  implicit class ThreeEffectsExtension(effects: (OneShotEffect, OneShotEffect, OneShotEffect)) extends EffectsSeqExtension(effects.productIterator.toSeq.ofType[OneShotEffect])

  case class DealEffectBuilder(objectIdentifier: Identifier[ObjectId], amount: Int) {
      def damageTo(recipientIdentifier: Identifier[ObjectOrPlayer]): OneShotEffect = DealDamageEffect(objectIdentifier, recipientIdentifier, amount)
  }
  case class ContinuousEffectBuilder(objectIdentifier: Identifier[ObjectId], continuousEffectDescriptions: Seq[ContinuousEffectDescription]) {
      def until(conditionDefinition: ConditionDefinition): OneShotEffect = ContinuousEffectCreationEffect(objectIdentifier, continuousEffectDescriptions, conditionDefinition)
  }

  implicit class ObjectIdentifierExtension(objectIdentifier: Identifier[ObjectId]) {
    def deals(amount: Int): DealEffectBuilder = DealEffectBuilder(objectIdentifier, amount)
    def apply(continuousEffectDescriptions: ContinuousEffectDescription*): ContinuousEffectBuilder = ContinuousEffectBuilder(objectIdentifier, continuousEffectDescriptions)
  }

  implicit class PlayerIdentifierExtension(playerIdentifier: Identifier[PlayerId]) {
    def gain(amount: Int) = new {
      def life: OneShotEffect = GainLifeEffect(playerIdentifier, amount)
    }
    def drawsACard: OneShotEffect = DrawsACardEffect(playerIdentifier)
  }

  def searchYourLibraryForA(objectFilter: Filter[ObjectId]): OneShotEffect = SearchLibraryEffect(objectFilter)
  def reveal(objectIdentifier: Identifier[ObjectId]): OneShotEffect = RevealEffect(objectIdentifier)
  def put(objectIdentifier: Identifier[ObjectId]) = new {
    def intoYourHand: OneShotEffect = PutIntoHandEffect(objectIdentifier)
  }
  def exile(objectIdentifier: Identifier[ObjectId]): OneShotEffect = ExileEffect(objectIdentifier)
  def put(number: Int, counterType: CounterType) = new {
    def on(objectIdentifier: Identifier[ObjectId]): OneShotEffect = PutCountersEffect(number, counterType, objectIdentifier)
  }
  def shuffle: OneShotEffect = ShuffleEffect
  def scry(number: Int): OneShotEffect = ScryEffect(number)
  def drawACard: OneShotEffect = DrawACardEffect
}
