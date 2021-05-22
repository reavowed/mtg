import cats.Monad

import scala.reflect.{ClassTag, classTag}
import cats.syntax.flatMap._
import cats.syntax.functor._

import scala.annotation.tailrec

package object mtg {
  implicit class AnyExtensionMethods[T](t: T) {
    def asOptionalInstanceOf[S : ClassTag]: Option[S] = if(classTag[S].runtimeClass.isInstance(t)) Some(t.asInstanceOf[S]) else None
  }
  implicit class SeqExtensionMethods[T](seq: Seq[T]) {
    def ofType[S : ClassTag]: Seq[S] = seq.collect {
      case s: S => s
    }
    def splitByType[S : ClassTag]: (Seq[S], Seq[T]) = seq.foldLeft((Seq.empty[S], Seq.empty[T])) { case ((ss, ts), t) =>
      t.asOptionalInstanceOf[S].map(s => (ss :+ s, ts)).getOrElse((ss, ts :+ t))
    }
    def takeWhileOfType[S : ClassTag]: Seq[S] = seq.takeWhile(classTag[S].runtimeClass.isInstance).as[S]
    def as[S: ClassTag]: Seq[S] = seq.map(_.asInstanceOf[S])
    def takeRightUntil(p: T => Boolean): Seq[T] = seq.reverse.takeWhile(t => !p(t)).reverse
    def mapFind[S](f: T => Option[S]): Option[S] = {
      seq.iterator.map(f).find(_.isDefined).flatten
    }
    def findIndex(p: T => Boolean): Option[Int] = {
      seq.zipWithIndex.find { case (t, _) => p(t) }.map(_._2)
    }
    def removeAtIndex(index: Int): Seq[T] = {
      seq.take(index) ++ seq.drop(index + 1)
    }
  }
  implicit class TupleExtensionMethods[A, B](tuple: (A, B)) {
    def mapLeft[C](f: A => C): (C, B) = (f(tuple._1), tuple._2)
    def mapRight[C](f: B => C): (A, C) = (tuple._1, f(tuple._2))
  }

  implicit object optionMonad extends Monad[Option] {
    def flatMap[A, B](fa: Option[A])(f: A => Option[B]): Option[B] = fa.flatMap(f)
    def pure[A](a: A): Option[A] = Some(a)

    @tailrec
    def tailRecM[A, B](a: A)(f: A => Option[Either[A, B]]): Option[B] = f(a) match {
      case None              => None
      case Some(Left(nextA)) => tailRecM(nextA)(f)
      case Some(Right(b))    => Some(b)
    }
  }

  implicit class SeqMonadExtensionMethods[F[_] : Monad, A](seq: Seq[F[A]]) {
    def swap: F[Seq[A]] = {
      seq.foldLeft(Monad[F].point(Seq.empty[A])) { (outputSeq, outputValue) =>
        for {
          seq <- outputSeq
          value <- outputValue
        } yield seq :+ value
      }
    }
  }

}
