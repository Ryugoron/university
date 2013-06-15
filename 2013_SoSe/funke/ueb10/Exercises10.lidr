> module Exercises10 -- no |where| here in Idris!

Exercises 10
------------

2.  Idris provides the |NAT|, |Vector|, and |FIN| as part of its
    library, they are called |Nat|, |Vect|, and |Fin|, defined in the
    files Prelude/Nat.idr, Prelude/Vect.idr, Prelude/Fin.idr.  From now
    on we'll use this versions, which, besides being already there,
    have the additional advantage of some special notation, for example:

> testVect1            :  Vect Char 3      --  can be used instead of 
>                                          --  |S (S (S O))|
> testVect1            =  ['d', 't', 'p']  --  can be used instead of 
>                                          --  |'d' :: 't' :: 'p' :: Nil|

    Implement a function |zipVect| similar to the Haskell standard |zip|,
    but which acts only on arrays of the same size.\\

Lösung:

> zipVect : Vect a n -> Vect b n -> Vect (a, b) n
> zipVect Nil Nil               = Nil
> zipVect (a :: as) (b :: bs)   = (a , b) :: zipVect as bs


3.  The most important notational (and computational) element introduced
    by Idris is the \emph{dependent function}, functions where not just
    the value, but also the type of the result depends on the value of 
    the argument.  As an example, consider a function that takes a
    natural number |n| and an element |a| and creates an array of |n|
    elements.  Therefore, the type of the result, namely |Vect a n|,
    and the value of the result, namely |[a, ..., a]| (|n| times), both
    depend on |n|.  This is declared in Idris as follows:

> repl                 :  (n : Nat) -> a -> Vect a n


    Implement |repl|.


> repl    O   a         = Nil
> repl (S n)  a         = a :: repl n a


4.  Consider the following expression: |zipVect testVect1 (repl n 0.0)|.
    Any choice of |n| except for |3| will lead to a type error.  Idris
    can, in fact, infer that the only possible value is |3| and so
    we shouldn't have to provide it ourselves.  We can achieve this
    effect by making the argument \emph{implicit}:

> repl'                :  {n : Nat} -> a -> Vect a n

    Now we can use |repl'| instead of |repl|: |zipVect testVect1 (repl' 0.0)|
    and Idris will indeed infer the correct value for the implicit |n|.
    Sometimes, however, we need to provide the argument explicitely, this
    is achieved by |repl' {n = m} a|, where |m| is the value we want to 
    provide, and |{ n = ... }| identifies the implicit argument whose
    value we're supplying.

    Implement |repl'|.

> repl' {n = O}     a   = Nil
> repl' {n = (S m)} a   = a :: repl' a

5.  We have, in fact, been using implicit arguments all along, for example

< repl                 :  (n : Nat) -> a -> Vect a n

    is equivalent to

< repl                 :  {a : Type} -> (n : Nat) -> a -> Vect a n

    In fact, other languages, such as Agda, do not allow the first version
    and insisit on having \emph{explicit} implicit arguments.  In Idris,
    only \emph{lowercase} arguments are allowed for implicit implicit arguments,
    i.e., the following will fail:

< repl                 :  (n : Nat) -> A -> Vect A n

    Give Agda-style type declarations for |Vect| and |zipVect| and typecheck
    them.

> data AgdaVec : Type -> Nat -> Type where
>   ANil         : {a : Type} -> AgdaVec a O
>   ACons        : {a : Type} -> {n : Nat} -> a -> AgdaVec a n -> AgdaVec a (S n)

> zipAVect : {a : Type} -> {b : Type} -> {n : Nat} -> AgdaVec a n -> AgdaVec b n -> AgdaVec (a,b) n
> zipAVect {n = O} ANil ANil                        = ANil
> zipAVect {n = (S m)} (ACons a as) (ACons b bs)    = ACons (a,b) (zipAVect as bs)

Tested with

< > zipAVect (ACons 1 (ACons 2 ANil)) (ACons 'a' (ACons 'b' ANil))
< ACons (1, 'a') (ACons (2, 'b') (ANil)) : AgdaVec (Int, Char) 2


6.  One of the fundamental datatypes in dependently-typed programming 
    languages is that of the \emph{dependent pair}:

> data DP              :  (ty : Type) -> (P : ty -> Type) -> Type where
>   dp                 :  (x : ty) -> p x -> DP ty p

    
    Thus, a value |v : DP ty p| has the form |dp x px|, where |x| has
    the type |ty| and |px| has the type |p x|.

    Give an Agda-style equivalent definition for |DP|, i.e. use
    explicit implicits instead of implicit implicits in |dp|.  Define
    selector functions |outl| and |outr| such that

< data DP   : (ty : Type) -> (P : ty -> Type) -> Type where
<   dp      : {ty : Type} -> {p : ty -> Type} -> (x : ty) -> (p x) -> DP ty p

> outl : {ty : Type} -> {P : ty -> Type} -> (DP ty P) -> ty
> outl (dp x px)       =  x

> outr : {ty : Type} -> {p : ty -> Type} -> (x : DP ty p) -> p (outl x)
> outr (dp x y)       =  y

A small Test. We have three different types in a list, dependent on the first argument of the tupel.

> testFun : Nat -> Type
> testFun O         = Nat
> testFun (S O)     = Char
> testFun (S (S n)) = Vect Nat 2

> testList : List (DP Nat testFun)
> testList = [(dp 0 5) , (dp 0 1) , (dp 1 'a') , (dp 1 'b') , (dp 4 [1, 2]) , (dp 5 [5,6])]
