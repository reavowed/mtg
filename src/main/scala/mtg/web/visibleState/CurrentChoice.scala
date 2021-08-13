package mtg.web.visibleState

import mtg.game.stack.ResolveEffectChoice
import mtg.game.state.{GameState, PlayerChoice}
import mtg.game.turns.priority.TriggeredAbilityChoice

case class PendingTriggeredAbilityDetails(id: Int, text: String, artDetails: ArtDetails)
case class TriggeredAbilityChoiceDetails(abilities: Seq[PendingTriggeredAbilityDetails])

case class CurrentChoice(`type`: String, playerToAct: String, details: Any)
object CurrentChoice {
  def apply(choice: PlayerChoice, gameState: GameState): CurrentChoice = {
    choice match {
      case resolveEffectChoice: ResolveEffectChoice =>
        CurrentChoice(
          resolveEffectChoice.effectChoice.getClass.getSimpleName,
          resolveEffectChoice.effectChoice.playerChoosing.id,
          resolveEffectChoice.effectChoice)
      case triggeredAbilityChoice: TriggeredAbilityChoice =>
        CurrentChoice(
          triggeredAbilityChoice.getClass.getSimpleName,
          triggeredAbilityChoice.playerToAct.id,
          TriggeredAbilityChoiceDetails(triggeredAbilityChoice.abilities.map(pendingAbility =>
            PendingTriggeredAbilityDetails(
              pendingAbility.id,
              pendingAbility.triggeredAbility.getText(gameState),
              ArtDetails.get(pendingAbility.triggeredAbility, gameState))
          )))
      case choice =>
        CurrentChoice(choice.getClass.getSimpleName, choice.playerToAct.id, choice)
    }
  }
}
