package mtg.sets.kaldheim

import mtg.cards.{CardInSetData, Set}
import mtg.sets.alpha.cards._
import mtg.sets.kaldheim.cards.{GnottvoldRecluse, GrizzledOutrider}

import java.time.{LocalDate, Month}

object Kaldheim extends Set(
  "Kaldheim",
  "KHM",
  LocalDate.of(2021, Month.FEBRUARY, 5),
  Seq(
    CardInSetData(GnottvoldRecluse, 172),
    CardInSetData(GrizzledOutrider, 173),
    CardInSetData(Plains, 394),
    CardInSetData(Island, 395),
    CardInSetData(Swamp, 396),
    CardInSetData(Mountain, 397),
    CardInSetData(Forest, 398)))
