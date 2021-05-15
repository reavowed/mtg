import scala.reflect.ClassTag

package object mtg {
  implicit class SeqExtensionMethods[T](seq: Seq[T]) {
    def ofType[S : ClassTag]: Seq[S] = seq.collect {
      case s: S => s
    }
    def takeWhileOfType[S : ClassTag]: Seq[S] = seq.takeWhile(_.isInstanceOf[S]).as[S]
    def as[S: ClassTag]: Seq[S] = seq.map(_.asInstanceOf[S])
  }

}
