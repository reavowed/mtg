package mtg.effects.number
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.utils.TextUtils

case class LiteralNumberIdentifier(number: Int) extends NumberIdentifier {
  override def get(gameState: GameState, resolutionContext: StackObjectResolutionContext): Int = number
  override def getText(followingWord: String): String = TextUtils.getNumberWord(number, followingWord)
  override def isSingular: Boolean = number == 1
}
