package mtg.utils

import monocle.Lens
import monocle.function.At

class AtGuaranteed[I, A] extends At[Map[I, A], I, A] {
  override def at(i: I): Lens[Map[I, A], A] = {
    Lens((_: Map[I, A])(i))(v => map => (map - i) + (i -> v))
  }
}

object AtGuaranteed {
  def apply[I, A] = new AtGuaranteed[I, A]
}
