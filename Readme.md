## traits vs. type classes

This is code is a study comparing trait based interfaces with type-class based interfaces in Scala.

Here is my conclusion derived from the code in [traits-vs-type-classes.scala](traits-vs-type-classes.scala)

Trait-based interface require less boiler plate and fewer language features and are thus simpler. Implicit classes are important here to let types you don't control implicitly extend desired interfaces.

However trait-based interfaces break down in the face of some generic traits for several reasons:
* One cannot implement the same generic trait twice with different argument types. In case of an interface Equal[T] that allow comparing with T, we can make a type comparable to both Int and String. Even implicit classes don't solve this, because they become ambiguous.
* Data types can't conditionally implement an interface depending on their own type arguments. We can't make a list of comparable things be comparable but a list of non-comparable things be non-comparable. Implicit classes solve this as long as the type parameters of the implemented interfaces don't depends on the types of method parameters.

Right now one has to choose between less boiler plate or more power and migration between the two requires significant refactoring.

Simulacrum is an annotation-macro based library reducing the required boiler-plate for type-classes. As a result of the above conclusions I created a proposal for reducing the boiler plate even more putting Simulacrum-generated type-classes on very equal level with trait-based interfaces. [Simulacrum-Proposal.md](Simulacrum-Proposal.md)