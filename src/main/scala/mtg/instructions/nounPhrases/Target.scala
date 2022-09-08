package mtg.instructions.nounPhrases

import mtg.continuousEffects.TargetPreventionEffect
import mtg.definitions.{ObjectId, ObjectOrPlayerId}
import mtg.effects.{EffectContext, InstructionResolutionContext}
import mtg.game.state.{GameState, StackObjectWithState}
import mtg.instructions.grammar.GrammaticalPerson
import mtg.instructions.nouns.ClassNoun

import scala.reflect.ClassTag

class Target[T <: ObjectOrPlayerId : ClassTag](noun: ClassNoun[T]) extends SingleIdentifyingNounPhrase[T] {
  override def getText(cardName: String): String = {
    "target " + noun.getSingular(cardName)
  }
  override def person: GrammaticalPerson = GrammaticalPerson.Third

  override def identifySingle(gameState: GameState, resolutionContext: InstructionResolutionContext): (T, InstructionResolutionContext) = {
    resolutionContext.popTarget.mapLeft(_.asInstanceOf[T])
  }
  def getValidChoices(source: StackObjectWithState, gameState: GameState, effectContext: EffectContext): Seq[ObjectOrPlayerId] = {
    (gameState.gameObjectState.allObjects.map(_.objectId) ++ gameState.gameData.playersInTurnOrder)
      .filter(isValidTarget(source, _, gameState, effectContext))
      .toSeq
  }
  def isValidTarget(source: StackObjectWithState, possibleTarget: ObjectOrPlayerId, gameState: GameState, effectContext: EffectContext): Boolean = {
    noun.getAll(gameState, effectContext).contains(possibleTarget) &&
      !gameState.gameObjectState.activeContinuousEffects.ofType[TargetPreventionEffect].exists(_.preventsTarget(source, possibleTarget, gameState))
  }
}

object Target {
  def apply(filter: ClassNoun[ObjectId]): Target[ObjectId] = new Target(filter)
  def getAll(stackObjectWithState: StackObjectWithState): Seq[Target[_]] = {
    stackObjectWithState.applicableInstructionParagraphs.flatMap(_.instructions).flatMap(_.targetIdentifiers)
  }
}
