package mtg.data.sets

import mtg.cards.{CardInSetData, Set}
import mtg.data.cards.alpha.{LightningBolt, SavannahLions}

import java.time.{LocalDate, Month}

object Alpha extends Set(
  "Alpha",
  "LEA",
  LocalDate.of(1993, Month.AUGUST, 5),
  Seq((
    CardInSetData(SavannahLions, 38)),
    CardInSetData(LightningBolt, 161))
)
