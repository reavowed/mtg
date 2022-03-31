package mtg.sets.alpha

import mtg.cards.{CardInSetData, Set}
import mtg.sets.alpha.cards.{AirElemental, LightningBolt, SavannahLions}

import java.time.{LocalDate, Month}

object Alpha extends Set(
  "Alpha",
  "LEA",
  LocalDate.of(1993, Month.AUGUST, 5),
  Seq(
    CardInSetData(SavannahLions, 38),
    CardInSetData(AirElemental, 46),
    CardInSetData(LightningBolt, 161))
)
