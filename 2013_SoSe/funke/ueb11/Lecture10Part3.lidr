> module Lecture10Part3

So far, we have completely ignored implementations.  It turns out,
however, that all the rules we have seen so far have natural
and useful implementations in terms of familiar datatypes, with
one exception: the rule of negation elimination.

> pair                 :  (a -> b, a -> c) -> a -> (b, c)
> pair (f, g) a        =  (f a, g a)

> And                  :  Type -> Type -> Type
> And p1 p2            =  (p1, p2)

In the following, we just use |(p1, p2)| instead of |And p1 p2|.

Introduction rule

> andI                 :  (p1, p2) -> (p1, p2)
> andI                 =  id

Elimination rules

> andEL                :  (p1, p2) -> p1
> andEL                =  fst
> andER                :  (p1, p2) -> p2
> andER                =  snd

Example:

> ex1                  :  (p1, p2) -> (p2, p1)
> ex1 (p1, p2)         =  (p2, p1)

> Or                   :  Type -> Type -> Type
> Or p1 p2             =  Either p1 p2

Introduction rules:

> orIL                 :  p1 -> Either p1 p2
> orIL                 =  Left
> orIR                 :  p2 -> Either p1 p2
> orIR                 =  Right

Elimination rule:

> orE                  :  (Either p1 p2, p1 -> p, p2 -> p) -> p
> orE (Left p1, f, g)  =  f p1
> orE (Right p2, f, g) =  g p2

Examples:

> ex2                  :  Either p1 p2 -> Either p2 p1
> ex2 (Left p1)        =  Right p1
> ex2 (Right p2)       =  Left p2


Negation

> Neg                  :  Type -> Type
> Neg p                =  p -> _|_  -- the type with no elements

> negI                 :  (p -> (q, q -> _|_ )) -> p -> _|_
> negI f p             =  let ctrd = f p in snd ctrd (fst ctrd)

> negE                 :  Neg (Neg p) -> p  -- Cannot be implemented!

Examples:

> ex3                  :  (p -> (p -> _|_)) -> p -> _|_
> ex3 f p              =  (f p) p

> {- 
> ex4                  :  Neg (Or p (Neg p)) -> p -- Cannot be implemented!
> -}

> ex5                  :  Neg (Or p (Neg p)) -> Neg p

> {-
> ex6                  :  Or p (Neg p) -- Cannot be implemented!
> -}

Universal quantification
------------------------

We want

< Forall               :  (a : Type) -> (p : a -> Type) -> Type
< Forall a p           =  (x : a) -> p x

but Idris doesn't (yet) let us have a dependent function type on
the RHS of a definition.  We'll just use |(x : a) -> p x| everywhere
instead of |Forall a p|.

> forallI              :  ((x : a) -> p x) -> ((x : a) -> p x) -- Forall a p
> forallI              =  id
> forallE              :  ((x : a) -> p x) -> (x : a) -> p x
> forallE f x          =  f x

Example:

> ex7                  :  ((x : a) -> (p x, q x)) ->
>                         ((x : a) -> p x, (x : a) -> q x)
> ex7 f                =  (\ x => fst (f x), \ x => snd (f x))

Compare this version with the previous one (the unimplemented |Forall|
and |And|).

The existential quantifier is implemented by the dependently-typed pair
(we use the Idris notation):

> Ex                   :  (a : Type) -> (p : a -> Type) -> Type
> Ex a p               =  (x : a ** p x)

> exI                  :  (x : a) -> p x -> (x : a ** p x)
> exI  x px            =  (x ** px)

> exE                  :  (x : a ** p x) -> ((x : a) -> p x -> q) -> q
> exE (x ** px) f      =  f x px


Example

> ex8                  :  (x : a ** ((y : b) -> r (x, y))) ->
>                         ((y : b) -> (x : a ** r (x, y)))

Identity:
--------

< data Id : (x1 : a) -> (x2 : a) -> Type where
<   refl  : (x : a) -> Id x x

Idris already defines this datatype in the Prelude, using the
syntax sugar |x1 = x2| instead of |Id x1 x2|, which we also
adopt:

> idI : (x : a) -> x = x
> idI x = refl


> idE : (x1 : a) -> (x2 : a) ->  x1 = x2  ->  p x1  -> p x2
> idE x1 x1 refl px1 = px1

|idE| is called |replace| in the Idris Prelude.

Example:

> symm : (x1 : a) -> (x2 : a) -> x1 = x2 -> x2 = x1
> symm x1 x1 refl = refl 

> tran : (x1 : a) -> (x2 : a) -> (x3 : a) ->
>         x1 = x2 -> x2 = x3 -> x1 = x3
> tran x1 x1 x1 refl refl  = refl 

Note that |symm| and |trans| are total functions, although it
might seem that we have only defined them for a subset of the
possible patterns.  By the way, you can check whether Idris 
thinks a function |f| is total by using the command |:total f|
at the Idris prompt.  Idris should answer with |Total|.

Most proofs really benefit from the concrete representation.  But can
we be sure that we cannot prove more using the concrete representation
than using the abstract one?  The answer is yes, we can.  This is a
famous theorem, going under the name ``the Curry-Howard isomorphism''.
