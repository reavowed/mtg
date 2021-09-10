package mtg.data.sets

import mtg.cards.{CardInSetData, Set}
import mtg.data.cards.m10.Divination
import mtg.data.cards.m21.{AlpineWatchdog, ConcordiaPegasus}

import java.time.{LocalDate, Month}

object Magic2010 extends Set(
  "Core Set 2021",
  "M21",
  LocalDate.of(2020, Month.JULY, 3),
  Seq(
    CardInSetData(Divination, 49)
  ))
