package mtg.web.visibleState

import mtg.game.ObjectId
import mtg.game.priority.TriggeredAbilityChoice
import mtg.game.state.{GameState, NewChoice, StackObjectWithState}
import mtg.stack.adding.ModeChoice
import mtg.stack.resolving.ResolveEffectChoice

case class PendingTriggeredAbilityDetails(id: Int, text: String, artDetails: ArtDetails)
case class TriggeredAbilityChoiceDetails(abilities: Seq[PendingTriggeredAbilityDetails])
case class ModeChoiceDetails(modes: Seq[String], stackObjectId: ObjectId, artDetails: ArtDetails)

case class CurrentChoice(`type`: String, playerToAct: String, details: Any)
object CurrentChoice {
  def apply(choice: NewChoice[_], gameState: GameState): CurrentChoice = {
    choice match {
      case resolveEffectChoice: ResolveEffectChoice =>
        CurrentChoice(
          resolveEffectChoice.effectChoice.getClass.getSimpleName,
          resolveEffectChoice.effectChoice.playerChoosing.toString,
          resolveEffectChoice.effectChoice)
      case triggeredAbilityChoice: TriggeredAbilityChoice =>
        CurrentChoice(
          triggeredAbilityChoice.getClass.getSimpleName,
          triggeredAbilityChoice.playerToAct.toString,
          TriggeredAbilityChoiceDetails(triggeredAbilityChoice.abilities.map(pendingAbility =>
            PendingTriggeredAbilityDetails(
              pendingAbility.id,
              pendingAbility.triggeredAbility.getText(gameState),
              ArtDetails.get(pendingAbility.triggeredAbility, gameState))
          )))
      case modeChoice: ModeChoice =>
        val stackObject = gameState.gameObjectState.getCurrentOrLastKnownState(modeChoice.stackObjectId).asInstanceOf[StackObjectWithState]
        CurrentChoice(
          modeChoice.getClass.getSimpleName,
          modeChoice.playerToAct.toString,
          ModeChoiceDetails(
            modeChoice.modes.map(_.getText(stackObject.characteristics.name.get)),
            modeChoice.stackObjectId,
            ArtDetails.get(stackObject.gameObject.underlyingObject, gameState)))
      case choice =>
        CurrentChoice(choice.getClass.getSimpleName, choice.playerToAct.toString, choice)
    }
  }
}
