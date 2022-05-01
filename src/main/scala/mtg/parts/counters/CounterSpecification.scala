package mtg.parts.counters

import mtg.utils.TextUtils

case class CounterSpecification(number: Int, counterType: CounterType) {
  def description: String = {
    def numberWord = TextUtils.getWord(number, counterType.description)
    def counterWord = if (number == 1) "counter" else "counters"
    Seq(numberWord, counterType.description, counterWord).mkString(" ")
  }
  def addToMap(map: Map[CounterType, Int]): Map[CounterType, Int] = {
    map.updatedWith(counterType)(_.map(_ + number).orElse(Some(number)))
  }
}
