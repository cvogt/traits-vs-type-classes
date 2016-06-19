object Main extends App{
  SymmetricInterface
  SymmetricTypeClass
  AsymmmetricInterface
  AsymmetricTypeClass
  HypotheticalGeneratedSymmetricTypeClass
}

object SymmetricInterface{
  // interfaces, whose type parameters don't depend on the right hand side
  // can be implemented without type classes
  sealed trait Equals[R] extends Any{
    def ===(right: R): Boolean
    def !==(right: R): Boolean = ! ==(right)
  }
  implicit class IntEquals( left: Int ) extends Equals[Int]{
    def ===(right: Int) = left == right
  }
  implicit class ListEquals[T]( left: List[T] )(implicit canEqual: T => Equals[T] ) extends Equals[List[T]]{
    def ===(right: List[T]) =
      left.size == right.size && (left zip right).forall{ case(l,  r) => l === r }
  }
  case class Person( name: String ) extends Equals[Person]{
    def ===(other: Person) = ==(other)
  }

  assert( 1 === 1 )
  assert( 1 !== 2 )
  assert( List(1,2) === List(1,2) )
  assert( List(1,2) !== List(1,2,3) )
  assert( Person("Chris") === Person("Chris") )
  assert( Person("Chris") !== Person("Miguel") )

  println("passed: SymmetricInterface")
}

object SymmetricTypeClass{
  // type-class implementation of AsymEquals
  sealed trait Equals[T]{
    def ===(left: T, right: T): Boolean
  }
  object Equals{
    implicit object intEquals extends Equals[Int]{
      def ===(left: Int, right: Int): Boolean = left == right
    }
    implicit object personEquals extends Equals[Person]{
      def ===(left: Person, right: Person): Boolean = left === right
    }
    implicit def listEquals[T](implicit asymEq:Equals[T]): Equals[List[T]] = new Equals[List[T]]{
      def ===(left: List[T], right: List[T]): Boolean =
        left.size == right.size && (left zip right).forall{ case(l,  r) => l === r }
    }
    implicit class Syntax[T](left:T){
      def ===(right:T)(implicit asymEq:Equals[T]) = asymEq.===(left, right)
      def !==(right:T)(implicit asymEq:Equals[T]) = ! ==(right)
    }
  }
  case class Person( name: String ){
    def ===(other: Person) = ==(other)
  }

  import Equals.Syntax

  assert( 1 === 1 )
  assert( 1 !== 2 )
  assert( List(1,2) === List(1,2) )
  assert( List(1,2) !== List(1,2,3) ) 
  assert( Person("Chris") === Person("Chris") )
  assert( Person("Chris") !== Person("Miguel") )

  println("passed: SymmetricTypeClass")
}


object AsymmmetricInterface{
  // interfaces, whose type parameters depend on the right hand side
  // cannot be implemented without type classes
  sealed trait AsymEquals[R] extends Any{
    def ===(right: R): Boolean
    def !==(right: R): Boolean = ! ==(right)
  }
  implicit class IntAsymEquals( val left: Int ) extends AnyVal with AsymEquals[Int]{
    override def ===(right: Int): Boolean = left == right
  }
  implicit class IntDoubleAsymEquals( val left: Int ) extends AnyVal with AsymEquals[Double]{
    override def ===(right: Double): Boolean = left == right
  }
  implicit class ListAsymEquals[T,R]( val left: List[T] )(implicit canEqual: T => AsymEquals[R] )
    extends AsymEquals[List[R]]{
    override def ===(right: List[R]): Boolean =
      left.size == right.size && (left zip right).forall{ case(l,  r) => l === r }
  }

  case class Person( name: String ) extends AsymEquals[Person]{
    def ===(other: Person) = ==(other)
  }

  // type error: ambiguous: both IntDoubleAsymEquals and IntAsymEquals are possible
  // assert( 1 === 1 )
  // assert( !( 1 === 2 ) )

  // type error: value === is not a member of List[Int]
  // assert( List(1,2) === List(1,2) )
  // assert( !( List(1,2) === List(1,2,3) ) ) 

  // no ambiguity because we only implemented comparisons with one type: Person
  assert( Person("Chris") === Person("Chris") )
  assert( Person("Chris") !== Person("Miguel") )

  println("passed: AsymmmetricInterface")
}

object AsymmetricTypeClass{
  sealed trait AsymEquals[T,R]{
    def ===(left: T, right: R): Boolean
  }
  object AsymEquals{
    implicit object intAsymEquals extends AsymEquals[Int, Int]{
      def ===(left: Int, right: Int): Boolean = left == right
    }
    implicit object intDoubleAsymEquals extends AsymEquals[Int, Double]{
      def ===(left: Int, right: Double): Boolean = left == right
    }
    implicit object personEquals extends AsymEquals[Person,Person]{
      def ===(left: Person, right: Person): Boolean = left === right
    }
    implicit def listAsymEquals[T,R](implicit asymEq:AsymEquals[T,R]): 
      AsymEquals[List[T], List[R]] = new AsymEquals[List[T], List[R]]{
      def ===(left: List[T], right: List[R]): Boolean =
        left.size == right.size && (left zip right).forall{ case(l,  r) => l === r }
    }
    implicit class Syntax[T](left:T){
      def ===[R](right:R)(implicit asymEq:AsymEquals[T,R]) = asymEq.===(left, right)
      def !==[R](right:R)(implicit asymEq:AsymEquals[T,R]) = ! ==(right)
    }
  }

  case class Person( name: String ){
    def ===(other: Person) = ==(other)
  }

  import AsymEquals.Syntax

  assert( 1 === 1 )
  assert( 1 === 1.0d )
  assert( 1 !== 2 )
  assert( List(1,2) === List(1.0,2.0) )
  assert( List(1,2) === List(1,2) )
  assert( List(1,2) !== List(1,2,3) ) 
  assert( Person("Chris") === Person("Chris") )
  assert( Person("Chris") !== Person("Miguel") )

  println("passed: AsymmmetricTypeClass")
}


object HypotheticalGeneratedSymmetricTypeClass{
  class typeclass extends annotation.StaticAnnotation
  class implements[T] extends annotation.StaticAnnotation
  // type-class implementation of AsymEquals
  @typeclass
  sealed trait Equals[T]{
    def ===(left: T, right: T): Boolean
  }
  /** generated by @typeclass */
  object Equals{
    implicit class ops[T](left:T){
      def ===(right:T)(implicit asymEq:Equals[T]) = asymEq.===(left, right)
      def !==(right:T)(implicit asymEq:Equals[T]) = ! ==(right)
    }
  }

  import Equals.ops

  @implements[Equals[Person]]
  case class Person( name: String ){
    def ===(other: Person) = ==(other)
  }

  /** generated by @implements1[Equals] */
  object Person{
    implicit object personEquals extends Equals[Person]{
      def ===(left: Person, right: Person): Boolean = left === right
    }
  }

  // manually defined for types out of our control
  implicit object intEquals extends Equals[Int]{
    def ===(left: Int, right: Int): Boolean = left == right
  }
  implicit def listEquals[T](implicit asymEq:Equals[T]): Equals[List[T]] = new Equals[List[T]]{
    def ===(left: List[T], right: List[T]): Boolean =
      left.size == right.size && (left zip right).forall{ case(l,  r) => l === r }
  }

  assert( 1 === 1 )
  assert( 1 !== 2 )
  assert( List(1,2) === List(1,2) )
  assert( List(1,2) !== List(1,2,3) ) 
  assert( Person("Chris") === Person("Chris") )
  assert( Person("Chris") !== Person("Miguel") )

  println("passed: HypotheticalGeneratedSymmetricTypeClass")
}
