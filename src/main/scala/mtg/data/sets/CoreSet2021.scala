package mtg.data.sets

import mtg.cards.{CardInSetData, Set}
import mtg.data.cards.m21.AlpineWatchdog

import java.time.{LocalDate, Month}

object CoreSet2021 extends Set(
  "Core Set 2021",
  "M21",
  LocalDate.of(2020, Month.JULY, 3),
  Seq(
    CardInSetData(AlpineWatchdog, 2)))
