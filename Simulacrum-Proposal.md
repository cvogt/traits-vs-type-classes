See https://github.com/mpilquist/simulacrum/issues/62

At the moment in Scala one has to choose between trait-based interfaces with less boiler-plate or type-class based interfaces that scale to more use cases. Real world code bases either use only one concept and suffer the consequences. Or they use both concepts as needed but suffer from less regularity, more controversy and signiciant mechanical refactoring when changing your mind in a case. Simulacrum reduces the required boiler plate overhead for type classes but does not eliminate it entirely.

This is a proposal for a small addition to Simulacrum that would remove the remaining boiler plate and make migration between trait based interfaces and type-class based interfaces much more seamless. In a way it is very much in the spirit of Scala to integrate these OO / FP concepts and it has very concrete practical benefits. And it would basically put Simulacrum on an equal level with trait-based interfaces with regards to syntactic overhead.

So we know that type classes are more flexible than OO interface implementation. This is especially true with generics. A Scala class can't implement `Equals[Int]` and `Equals[String]` at the same time. Implicit classes can help here, but they can't be inferred if the generic types depend on method arguments, not only on the left hand sides. Type-classes solve this problem.

But even with Simulacrum the required boiler plate can be significant. This is particularly annoying when the use case at hand does not require the additional flexibility. Here is an example:

```scala
  @typeclass
  sealed trait Equals[T]{
    def ===(left: T, right: T): Boolean
  }
  case class Person( name: String )
  object Person{
    implicit object personEquals extends Equals[Person]{
      def ===(left: Person, right: Person): Boolean = left.name == right.name
    }
  }
```

requires more boiler plate than


```scala
  sealed trait Equals[R]{
    def ===(right: R): Boolean
  }
  case class Person( name: String ) extends Equals[Person]{
    def ===(other: Person) = this.name == other.name
  }
```

This adds up when one has to do it everywhere. So here is my proposal to get the best of both worlds. We could extend Simulacrum with the following annotation macro, that allows "implementing" type classes directly from data types.

```scala
  class implements[T] extends annotation.StaticAnnotation{ ... }
```

The usage would be as such:

```scala
  @typeclass
  sealed trait Equals[T]{
    def ===(left: T, right: T): Boolean
  }
  @implements[Equals[Person]]
  case class Person( name: String ){
    def ===(other: Person) = this.name == other.name
  }
```

This would automatically generate the following companion which simply forwards to the method on the class.

```scala
  object Person{
    implicit object EqualsInstance extends Equals[Person]{
      def ===(left: Person, right: Person): Boolean = left === right
    }
  }
```

`@typeclass` is the equivalent to OO `class`. `@implements` is the equivalent to `extends`.

This would cut down on boiler plate and would make migration between the two concepts much more seamless. It's conceivable to additionaly generate an actual interface trait allowing even easier migration.

