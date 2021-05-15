import scala.reflect.{ClassTag, classTag}

package object mtg {
  implicit class SeqExtensionMethods[T](seq: Seq[T]) {
    def ofType[S : ClassTag]: Seq[S] = seq.collect {
      case s: S => s
    }
    def takeWhileOfType[S : ClassTag]: Seq[S] = seq.takeWhile(classTag[S].runtimeClass.isInstance).as[S]
    def as[S: ClassTag]: Seq[S] = seq.map(_.asInstanceOf[S])
  }
  implicit class TupleOps[A, B](tuple: (A, B)) {
    def mapLeft[C](f: A => C): (C, B) = (f(tuple._1), tuple._2)
  }

}
