package mtg.web.visibleState

import mtg.game.actions.ResolveEffectChoice
import mtg.game.state.PlayerChoice

case class CurrentChoice(`type`: String, playerToAct: String, details: Any)
object CurrentChoice {
  def apply(choice: PlayerChoice): CurrentChoice = {
    choice.asOptionalInstanceOf[ResolveEffectChoice].map(_.effectChoice)
      .map(effectChoice => CurrentChoice(effectChoice.getClass.getSimpleName, effectChoice.playerChoosing.id, effectChoice))
      .getOrElse(CurrentChoice(choice.getClass.getSimpleName, choice.playerToAct.id, choice))
  }
}
