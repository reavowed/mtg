import cats.Monad
import cats.syntax.flatMap._
import cats.syntax.functor._

import scala.annotation.tailrec
import scala.collection.{IterableOps, View}
import scala.reflect.{ClassTag, classTag}

package object mtg {
  implicit class AnyExtensionMethods[T](t: T) {
    def asOptionalInstanceOf[S : ClassTag]: Option[S] = if(classTag[S].runtimeClass.isInstance(t)) Some(t.asInstanceOf[S]) else None
  }
  implicit class IterableExtensionMethods[A, CC[_]](seq: IterableOps[A, CC, CC[A]]) {
    def ofType[B : ClassTag]: CC[B] = seq.collect {
      case b: B => b
    }
    def findOption[B](f: A => Option[B]): Option[B] = seq.iterator.map(f).collectFirst {
      case Some(b) => b
    }
  }
  implicit class SeqExtensionMethods[T](seq: Seq[T]) {
    def splitByType[S : ClassTag]: (Seq[S], Seq[T]) = seq.foldLeft((Seq.empty[S], Seq.empty[T])) { case ((ss, ts), t) =>
      t.asOptionalInstanceOf[S].map(s => (ss :+ s, ts)).getOrElse((ss, ts :+ t))
    }
    def takeWhileOfType[S : ClassTag]: Seq[S] = seq.takeWhile(classTag[S].runtimeClass.isInstance).as[S]
    def as[S: ClassTag]: Seq[S] = seq.map(_.asInstanceOf[S])
    def takeRightUntil(p: T => Boolean): Seq[T] = seq.reverse.takeWhile(t => !p(t)).reverse
    def mapFind[S](f: T => Option[S]): Option[S] = {
      seq.iterator.map(f).find(_.isDefined).flatten
    }
    def mapWithIndex[S](f: (T, Int) => S): Seq[S] = {
      seq.zipWithIndex.map(f.tupled)
    }
    def findIndex(p: T => Boolean): Option[Int] = {
      seq.zipWithIndex.find { case (t, _) => p(t) }.map(_._2)
    }
    def removeAtIndex(index: Int): Seq[T] = {
      seq.take(index) ++ seq.drop(index + 1)
    }
    def single: T = seq match {
      case t +: Nil => t
      case Nil => throw new RuntimeException("Seq was empty")
      case _ => throw new RuntimeException("Seq contained multiple elements")
    }
    def insertAtIndex(t: T, index: Int): Seq[T] = {
      (seq.take(index) :+ t) ++ seq.drop(index)
    }
  }
  implicit class ViewExtensionMethods[T](view: View[T]) {
    def ofType[S : ClassTag]: View[S] = view.collect {
      case t if classTag[S].runtimeClass.isInstance(t) => t.asInstanceOf[S]
    }
    def mapFind[S](f: T => Option[S]): Option[S] = {
      view.map(f).find(_.isDefined).flatten
    }
    def mapCollect[S](f: T => Option[S]): View[S] = {
      view.collect(Function.unlift(f))
    }
    def single: T = {
      val iterator = view.iterator
      if (!iterator.hasNext) throw new RuntimeException("View was empty")
      val t = iterator.next()
      if (iterator.hasNext) throw new RuntimeException("View contained multiple elements")
      t
    }
  }
  implicit class TupleExtensionMethods[A, B](tuple: (A, B)) {
    def mapLeft[C](f: A => C): (C, B) = (f(tuple._1), tuple._2)
    def mapRight[C](f: B => C): (A, C) = (tuple._1, f(tuple._2))
  }
  implicit class TupleSeqExtensionMethods[A, B](tupleView: View[(A, B)]) {
    def ofLeftType[C : ClassTag]: View[(C, B)] = tupleView.collect {
      case (a, b) if classTag[C].runtimeClass.isInstance(a) => (a.asInstanceOf[C], b)
    }
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
