package mtg.utils

object MapUtils {
  implicit class NumericMapExtension[T](map: Map[T, Int]) {
    def add(otherMap: Map[T, Int]): Map[T, Int] = {
      otherMap.foldLeft(map) {
        case (map, (key, number)) => map.updatedWith(key)(_.map(_ + number).orElse(Some(number)))
      }
    }
  }
}
