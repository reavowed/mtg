package mtg.data.sets

import mtg.cards.{CardInSetData, Set}
import mtg.data.cards._
import mtg.data.cards.strixhaven.{AgelessGuardian, SpinedKarok}

import java.time.{LocalDate, Month}

object Strixhaven extends Set(
  "Strixhaven",
  "STX",
  LocalDate.of(2021, Month.APRIL, 23),
  Seq(
    CardInSetData(AgelessGuardian, 8),
    CardInSetData(SpinedKarok, 143),
    CardInSetData(Plains, 366),
    CardInSetData(Plains, 367),
    CardInSetData(Island, 368),
    CardInSetData(Island, 369),
    CardInSetData(Swamp, 370),
    CardInSetData(Swamp, 371),
    CardInSetData(Mountain, 372),
    CardInSetData(Mountain, 373),
    CardInSetData(Forest, 374),
    CardInSetData(Forest, 375)))
