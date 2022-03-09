package mtg.stack.adding

import mtg.core.symbols.ManaSymbol
import mtg.game.objects.ManaObject

import scala.annotation.tailrec

case class ManaPaymentDetails(manaToPay: Seq[ManaObject], manaPaid: Seq[ManaObject]) {
  def payWhere(f: ManaObject => Boolean): Option[ManaPaymentDetails] = {
    manaToPay.findIndex(f).map(index => ManaPaymentDetails(manaToPay.removeAtIndex(index), manaPaid :+ manaToPay(index)))
  }
  def payAll(): ManaPaymentDetails = ManaPaymentDetails(Nil, manaPaid ++ manaToPay)
}
object ManaPaymentDetails {
  def apply(manaToPay: Seq[ManaObject]): ManaPaymentDetails = ManaPaymentDetails(manaToPay, Nil)
}


object ManaCostAutoPayer {
  private def autoPayColoredCosts(symbols: Seq[ManaSymbol], manaPaymentDetails: ManaPaymentDetails): (Seq[ManaSymbol], ManaPaymentDetails) = {
    @tailrec
    def helper(uncheckedSymbols: Seq[ManaSymbol], unpayableSymbols: Seq[ManaSymbol], manaPaymentDetails: ManaPaymentDetails): (Seq[ManaSymbol], ManaPaymentDetails) = {
      uncheckedSymbols match {
        case (symbol: ManaSymbol.ForType) +: remainingSymbols =>
          manaPaymentDetails.payWhere(_.manaType == symbol.manaType) match {
            case Some(newManaPaymentDetails) =>
              helper(remainingSymbols, unpayableSymbols, newManaPaymentDetails)
            case None =>
              helper(remainingSymbols, unpayableSymbols :+ symbol, manaPaymentDetails)
          }
        case otherSymbol +: remainingSymbols =>
            helper(remainingSymbols, unpayableSymbols :+ otherSymbol, manaPaymentDetails)
        case Nil =>
          (unpayableSymbols, manaPaymentDetails)
      }
    }
    helper(symbols, Nil, manaPaymentDetails)
  }
  private def autoPayGenericCosts(manaSymbols: Seq[ManaSymbol], manaPaymentDetails: ManaPaymentDetails): (Seq[ManaSymbol], ManaPaymentDetails) = {
    reduceGenericManaCost(manaSymbols, manaPaymentDetails.manaToPay.length) match {
      case Some(remainingCost) =>
        (remainingCost, manaPaymentDetails.payAll())
      case None =>
        (manaSymbols, manaPaymentDetails)
    }
  }
  private def reduceGenericManaCost(manaSymbols: Seq[ManaSymbol], amount: Int): Option[Seq[ManaSymbol]] = {
    def helper(processedManaSymbols: Seq[ManaSymbol], manaSymbolsRemaining: Seq[ManaSymbol], amountRemaining: Int): Option[Seq[ManaSymbol]] = {
      manaSymbolsRemaining match {
        case ManaSymbol.Generic(symbolAmount) +: tail if symbolAmount == amountRemaining =>
          Some(processedManaSymbols ++ tail)
        case ManaSymbol.Generic(symbolAmount) +: tail if symbolAmount > amountRemaining =>
          Some((processedManaSymbols :+ ManaSymbol.Generic(symbolAmount - amountRemaining)) ++ tail)
        case ManaSymbol.Generic(symbolAmount) +: tail if symbolAmount < amountRemaining =>
          helper(processedManaSymbols, tail, amountRemaining - symbolAmount)
        case otherSymbol +: tail =>
          helper(processedManaSymbols :+ otherSymbol, tail, amountRemaining)
        case Nil =>
          None
      }
    }
    helper(Nil, manaSymbols, amount)
  }
  def payManaAutomatically(manaSymbols: Seq[ManaSymbol], manaToPay: Seq[ManaObject]): (Seq[ManaSymbol], Seq[ManaObject]) = {
    val manaPaymentDetails = ManaPaymentDetails(manaToPay, Nil)
    val (symbolsAfterColoredCosts, manaAfterColoredCosts) = autoPayColoredCosts(manaSymbols, manaPaymentDetails)
    val (symbolsAfterGenericCosts, manaAfterGenericCosts) = autoPayGenericCosts(symbolsAfterColoredCosts, manaAfterColoredCosts)
    (symbolsAfterGenericCosts, manaAfterGenericCosts.manaPaid)
  }
}
