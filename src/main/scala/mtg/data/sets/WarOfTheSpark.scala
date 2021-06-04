package mtg.data.sets

import mtg.cards.{CardInSetData, Set}
import mtg.data.cards.warofthespark.WardscaleCrocodile

import java.time.{LocalDate, Month}

object WarOfTheSpark extends Set(
  "War of the Spark",
  "WAR",
  LocalDate.of(2019, Month.MAY, 3),
  Seq(CardInSetData(WardscaleCrocodile, 183)))
