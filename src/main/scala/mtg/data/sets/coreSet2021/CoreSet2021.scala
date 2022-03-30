package mtg.data.sets.coreSet2021

import mtg.cards.{CardInSetData, Set}
import mtg.data.sets.coreSet2021.cards.{AlpineWatchdog, ConcordiaPegasus}

import java.time.{LocalDate, Month}

object CoreSet2021 extends Set(
  "Core Set 2021",
  "M21",
  LocalDate.of(2020, Month.JULY, 3),
  Seq(
    CardInSetData(AlpineWatchdog, 2),
    CardInSetData(ConcordiaPegasus, 12),
  ))
