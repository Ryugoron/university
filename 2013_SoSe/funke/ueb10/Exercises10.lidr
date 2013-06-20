> module Exercises10 -- no |where| here in Idris!

Exercises 10
------------

%%%%%%
%% Task 1
%%%%%%

It worked...


%%%%%%%
%% Task 2
%%%%%%%
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
%%%%%%
%% Solution Task 2
%%%%%%
Lösung:

Just as in the non dependent case we wright down the rekursion,
but there are only two cases, because we know both lists have the same length.

> zipVect : Vect a n -> Vect b n -> Vect (a, b) n
> zipVect Nil Nil               = Nil
> zipVect (a :: as) (b :: bs)   = (a , b) :: zipVect as bs


%%%%%%%
%% Task 3
%%%%%%%

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
%%%%%%%
%% Solution Task 3
%%%%%%%

We are given a natural Number and same Element of type $a$ and
return 
(1) The empty vector if, because it is the only one of size Zero
(2) A vector with $a$ and a repl of size $n$. This is by def. of (::) a
    Vector of size (S n) and values of type a, which shoud be the result
    by the type declaration.

> repl    O   a         = Nil
> repl (S n)  a         = a :: repl n a


%%%%%%%%%
%% Task 4
%%%%%%%%

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

%%%%%%%%%%
%% Solution Task 4
%%%%%%%%%%

We need to match the implicit input, to pattern match, wether we should
return an empty Vector or the recursion.

In the body of the iteration the parameter is implicitly given, because
we know, that $n$ is greater 0 and the result has to be of type |Vector a (S n)|
therefor the implicit parameter passed will be $n$.

> repl' {n = O}     a   = Nil
> repl' {n = (S m)} a   = a :: repl' a

%%%%%%%%%
%% Task 5
%%%%%%%%%
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

%%%%%%%%
%% Solution Task 5
%%%%%%%%

We just added every Typevariable we previously used implicitly to the Signature.
This is first the Type |a| and secondly the natural number |n|.

> data AgdaVec : Type -> Nat -> Type where
>   ANil         : {a : Type} -> AgdaVec a O
>   ACons        : {a : Type} -> {n : Nat} -> a -> AgdaVec a n -> AgdaVec a (S n)

With zip we do not think we need to match the natural number in the body,
but in the type we again have to introduce the type variables |a, b| and the natural number |n|.

> zipAVect : {a : Type} -> {b : Type} -> {n : Nat} -> AgdaVec a n -> AgdaVec b n -> AgdaVec (a,b) n
> zipAVect {n = O} ANil ANil                        = ANil
> zipAVect {n = (S m)} (ACons a as) (ACons b bs)    = ACons (a,b) (zipAVect as bs)

Tested with


< > zipAVect (ACons 1 (ACons 2 ANil)) (ACons 'a' (ACons 'b' ANil))
< ACons (1, 'a') (ACons (2, 'b') (ANil)) : AgdaVec (Int, Char) 2


%%%%%%%%%
%% Task 6
%%%%%%%%%
6.  One of the fundamental datatypes in dependently-typed programming 
    languages is that of the \emph{dependent pair}:

> data DP              :  (ty : Type) -> (P : ty -> Type) -> Type where
>   dp                 :  (x : ty) -> p x -> DP ty p

    
    Thus, a value |v : DP ty p| has the form |dp x px|, where |x| has
    the type |ty| and |px| has the type |p x|.

    Give an Agda-style equivalent definition for |DP|, i.e. use
    explicit implicits instead of implicit implicits in |dp|.  Define
    selector functions |outl| and |outr| such that

%%%%%%%%%
%% Task 6 Solution
%%%%%%%%

We intoduced (commented at the moment) for every implicit type variable
first the implicit declaration, these are |ty : Type| the type of the
first element of the tupel and the function |p : ty -> Type| the
function that determines the type of the second argument depending on the first.

< data DP   : (ty : Type) -> (P : ty -> Type) -> Type where
<   dp      : {ty : Type} -> {p : ty -> Type} -> (x : ty) -> (p x) -> DP ty p


For the left argument, we already know the type.
We get a type |ty : Type| for the first argument
and the reslt will simply be this type.

> outl : {ty : Type} -> {P : ty -> Type} -> (DP ty P) -> ty
> outl (dp x px)       =  x


To get the second argument we need to know the function for the second type
|p : ty -> Type|. But to get the type of the result, we have to know
what is the first argument, otherwise we would not be able to know the resulting type.

But we already wrote a function to compute the second argument |outl|.
If we apply | p (outl x)| if |x : DP ty p| the input, then this is the type
of the result.

> outr : {ty : Type} -> {p : ty -> Type} -> (x : DP ty p) -> p (outl x)
> outr (dp x y)       =  y

%%%%%%
%% Task 6 Test
%%%%%%
A small Test. We have three different types in a list, dependent on the first argument of the tupel.
This could be interpreted as an Indexed Sum Type.

> testFun : Nat -> Type
> testFun O         = Nat
> testFun (S O)     = Char
> testFun (S (S n)) = Vect Nat 2

> testList : List (DP Nat testFun)
> testList = [(dp 0 5) , (dp 0 1) , (dp 1 'a') , (dp 1 'b') , (dp 4 [1, 2]) , (dp 5 [5,6])]
