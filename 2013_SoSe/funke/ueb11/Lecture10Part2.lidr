> module Lecture10Part2



> pair                 :  (a -> b, a -> c) -> a -> (b, c)
> pair (f, g) a        =  (f a, g a)

> And                  :  Type -> Type -> Type

Introduction rule

> andI                 :  (p1, p2) -> And p1 p2

Elimination rules

> andEL                :  And p1 p2 -> p1
> andER                :  And p1 p2 -> p2

Example:

> ex1                  :  And p1 p2 -> And p2 p1
> ex1                  =  andI . pair (andER, andEL)

> Or                   :  Type -> Type -> Type

Introduction rules:

> orIL                 :  p1 -> Or p1 p2
> orIR                 :  p2 -> Or p1 p2

Elimination rule:

> orE                  :  (Or p1 p2, p1 -> p, p2 -> p) -> p

Examples:

> ex2                  :  Or p1 p2 -> Or p2 p1


Negation

> Neg                  :  Type -> Type

> negI                 :  (p -> And q (Neg q)) -> Neg p
> negE                 :  Neg (Neg p) -> p

Examples:

> ex3                  :  (p -> Neg p) -> Neg p
> ex3 f                =  negI (andI . pair (id, f))

> ex4                  :  Neg (Or p (Neg p)) -> p
> ex4 {p} no           =  negE (negI {q = Or p (Neg p)} 
>                                   (\ np => andI {p1 = Or p (Neg p)} 
>                                            (orIR {p1 = p} {p2 = Neg p} np, no)))


> ex5                  :  Neg (Or p (Neg p)) -> Neg p

> ex6                  :  Or p (Neg p)


Moral:
                +----------------------------------+
                |                                  |
                  Proof Checking  =  Type Checking |
                |                                  |
                +----------------------------------+

---------------------------------------------------------------------

Everything until here is copy-and-paste plus a few tweaks from the
.lhs file.  Besides the usual transformations of |::| to |:| and
|->| to |=>|, we also have to give more information to the type
checker for the last three examples.

We can now introduce quantifiers:

For all:

Introduction:

                       x : a     <-- x ``fresh''
                        .
                        .
                        .
                        p x
                     ----------      forallI
                      Forall a p

Elimination:

                      Forall a p
                        x : a
                     ------------   forallE
                         p x

In our notation, this becomes:

> Forall               :  (a : Type) -> (p : a -> Type) -> Type

> forallI              :  ((x : a) -> p x) -> Forall a p

> forallE              :  Forall a p -> (x : a) -> p x

Example:

> ex7                  :  Forall a (\ x => And (p x) (q x)) ->
>                         And (Forall a p) (Forall a q)
> ex7 {a} {p} {q} f    = 
>       andI  {p1 = Forall a p} {p2 = Forall a q} 
>             (forallI (\ x => andEL {p1 = p x} {p2 = q x} (forallE f x)),
>              forallI (\ x => andER {p1 = p x} {p2 = q x} (forallE f x)))


The existential quantifier has a more interesting elimination rule.

Introduction:

                                               .
                   x : a    p x    
                 ----------------  exI
                      Ex a p

Elimination:

                        x : a   <-- x ``fresh''
                         p x
                          .
                          .
                          .
           Ex a p         q
          -----------------------  exI
                    q 


In our notation (which also makes clear all this ``fresh'' vs.
given business), this becomes:


> Ex                   :  (a : Type) -> (p : a -> Type) -> Type

> exI                  :  (x : a) -> p x -> Ex a p

> exE                  :  Ex a p -> ((x : a) -> p x -> q) -> q


Example

> ex8                  :  Ex a (\ x => Forall b (\ y => r (x, y))) ->
>                         Forall b (\ y => Ex a (\ x => r (x, y)))

> ex8 {a} {b} {r} e    =  exE e f
>       where
>       f       :  (x : a) -> Forall b (\ y => r (x, y)) -> 
>                  Forall b (\ y => Ex a (\ z => r (z, y)))
>       f x fa  =  ?foo

Finally, we have one last element of standard first-order (or 
higher-order) logic, namely the identity:

Introduction:
                    x : a
                 ------------- idI
                    x = x

Elimination:

           x1 : a     x2 : a      x1 = x2      P x1
          -------------------------------------------  idE
                           P x2

All the properties of |x1| hold of |x2| as well, if |x1 = x2|.

In our notation, we have

> Id : (x1 : a) -> (x2 : a) -> Type

> idI : (x : a) -> Id x x

> idE : (x1 : a) -> (x2 : a) ->  Id x1 x2  ->  p x1  -> p x2

Example:

> symm : (x1 : a) -> (x2 : a) -> Id x1 x2 -> Id x2 x1
> symm x1 x2 i = idE {p = \ x => Id x x1} x1 x2 i (idI x1)

> tran : (x1 : a) -> (x2 : a) -> (x3 : a) ->
>         Id x1 x2 -> Id x2 x3 -> Id x1 x3
> tran x1 x2 x3 i12 i23 = idE {p = \ x => Id x1 x} x2 x3 i23 i12

As before, we have only used the typings of the rules.  We have not
given any implementations (Idris does not require implemenations in
order to type-check declarations).


