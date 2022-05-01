package mtg.continuousEffects

import mtg.game.state.DirectGameObjectAction

trait ReplacementEffect extends ContinuousEffect {
  def replaceAction(action: DirectGameObjectAction[_]): Option[DirectGameObjectAction[_]]
}
