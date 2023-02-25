package mtg.instructions

import mtg.effects.InstructionResolutionContext
import mtg.game.state.{GameAction, GameState}

import scala.language.implicitConversions

trait InstructionAction {
  def apply(context: InstructionResolutionContext): GameAction[InstructionResolutionContext]
}

object InstructionAction {
  trait WithResult[+T] {
    def apply(context: InstructionResolutionContext): GameAction[(T, InstructionResolutionContext)]
    def map[S](f: T => S): WithResult[S] = (context: InstructionResolutionContext) => {
      apply(context).map(_.mapLeft(f))
    }
    def flatMap[S](f: T => InstructionAction.WithResult[S]): WithResult[S] = context => for {
      (t, context) <- apply(context)
      (s, context) <- f(t).apply(context)
    } yield (s, context)

    def flatMap(f: T => InstructionAction): InstructionAction = context => for {
      (t, context) <- apply(context)
      context <- f(t).apply(context)
    } yield context
  }
  object WithResult {
    def apply[T](constructor: (InstructionResolutionContext, GameState) => GameAction[(T, InstructionResolutionContext)]): InstructionAction.WithResult[T] = {
      (context: InstructionResolutionContext) => GameAction.constructorAsAction(constructor(context, _))
    }
    def withoutContextUpdate[T](constructor: GameState => GameAction[T]): InstructionAction.WithResult[T] = {
      (context: InstructionResolutionContext) => GameAction.constructorAsAction(constructor).map(t => (t, context))
    }
    def withoutContextUpdate[T](constructor: (InstructionResolutionContext, GameState) => GameAction[T]): InstructionAction.WithResult[T] = {
      (context: InstructionResolutionContext) => GameAction.constructorAsAction(constructor(context, _)).map(t => (t, context))
    }
    implicit def directlyFromContext[T](f: InstructionResolutionContext => T): WithResult[T] = context => (f(context), context)
  }

  def apply(constructor: (InstructionResolutionContext, GameState) => GameAction[InstructionResolutionContext]): InstructionAction = {
    (context: InstructionResolutionContext) => GameAction.constructorAsAction(constructor(context, _))
  }

  def delegating(constructor: InstructionResolutionContext => InstructionAction): InstructionAction = {
    (context: InstructionResolutionContext) => constructor(context)(context)
  }
  def withoutContextUpdate(constructor: (InstructionResolutionContext, GameState) => GameAction[Any]): InstructionAction = {
    (context: InstructionResolutionContext) => GameAction.constructorAsAction(constructor(context, _)).map(_ => context)
  }
  def withoutContextUpdate(constructor: GameState => GameAction[Any]): InstructionAction = {
    (context: InstructionResolutionContext) => GameAction.constructorAsAction(constructor).map(_ => context)
  }

  implicit def fromGameAction(gameAction: GameAction[Any]): InstructionAction = {
    (context: InstructionResolutionContext) => gameAction.map(_ => context)
  }
}
