> module Lecture7summary where

> cross (f, g) (a, b) = (f a, g b)

A simple term language:

> data Term                      =  Con Float | Add Term Term | Div Term Term
>                                   deriving (Show)

> foldTerm fC fA fD (Con x)      =  fC  x
> foldTerm fC fA fD (Add t1 t2)  =  fA  (foldTerm fC fA fD t1) 
>                                       (foldTerm fC fA fD t2)
> foldTerm fC fA fD (Div t1 t2)  =  fD  (foldTerm fC fA fD t1) 
>                                       (foldTerm fC fA fD t2)

> eval0                         ::  Term -> Float
> eval0                          =  foldTerm id (+) (/)

> answer, wrong                 ::  Term
> answer                         =  Div (Add (Con 2.2) (Con 0.5)) (Con 1.813)
> wrong                          =  Div (Con 0.2) (Div (Con 1) (Con 0))

Evaluating |wrong| gives |0.0|.  That's because |1 / 0 = Infinity|, and
|0.2 / Infinity = 0|.  We might prefer not hiding division by zero errors.

> eval1                         ::  Term -> Float
> eval1                          =  foldTerm id (+) fD
>                                   where
>                                   fD x1 x2  =  if x2 == 0 
>                                                   then error "Division by zero!"
>                                                   else (x1 / x2)

This is a somewhat brutal solution: |error| just aborts the computation.
We might want to be a bit more refined, and return an exception:

> type Exception                 =  String
> data Exc a                     =  Raise Exception | Return a

> instance Show a => Show (Exc a) where
>   show (Raise e)               =  "Exception: " ++ e
>   show (Return a)              =  show a

> eval2                         ::  Term -> Exc Float
> eval2                          =  foldTerm Return fA fD
>   where
>     fA e1 e2  =  case e1 of
>                    Raise e    ->  Raise e
>                    Return x1  ->  case e2 of
>                                   Raise e    ->  Raise e
>                                   Return x2  ->  Return (x1 + x2)
>     fD e1 e2  =  case e1 of
>                    Raise e    ->  Raise e
>                    Return x1  ->  case e2 of
>                                   Raise e    ->  Raise e
>                                   Return x2  ->  if x2 == 0 then
>                                                   Raise "DIV BY ZERO!"
>                                                   else Return (x1 / x2)

But this is very low quality programming.  For one thing, there's a lot of
code duplication: not just between |fA| and |fD| which are almost
identical, but within the body of these functions themselves, with the
repeated

<     case e_i of
<          Raise e     ->  Raise e
<          Return x_i  ->  ...       -- some function using x_i

Abstracting this out, we obtain:

> op                            ::  Exc a  ->  (a  ->  Exc b)  ->  Exc b
> op eV f                        =  case eV of
>                                    Raise   e  ->  Raise e
>                                    Return  x  ->  f x

We can now re-write the evaluator in a more concise form:

> eval3                         ::  Term -> Exc Float
> eval3                          =  foldTerm Return fA fD
>   where
>   fA e1 e2  =  e1 `op` (\ x1  ->
>                e2 `op` (\ x2  ->
>                Return (x1 + x2)))

>   fD e1 e2  =  e1 `op` (\ x1  ->
>                e2 `op` (\ x2  ->
>                if  x2 == 0  then  Raise "DIV BY ZERO!"
>                             else  Return (x1 / x2)))

Suppose we want to keep track of the number of operations we are making.
For example, we could pass around a state consisting of the two
counts so far.

> type State = (Int, Int)

> incA :: State -> State
> incA = cross ((+1), id)

> incD :: State -> State
> incD = cross (id, (+1))

When writing the evaluator, the first function we want to define should
tell us what we do when we encounter a |Con x| term.  The answer is
``return |x| and leave the counter unchanged'', but the type of |fC| is
|Float -> ... | and not |(Float, Counter) -> ...|.  Thus, we need to
return a function, which will take a counter and just pass it along,
together with our |x|.  The type we are looking for is therefore that
of ``state transformers'':

> newtype St a   =  S (State -> (a, State))

> apply         ::  St a -> State -> (a, State)
> apply (S f) s  =  f s


> instance Show a => Show (St a) where
>   show st      =  "Value: " ++ show v ++ 
>                   ", Additions: " ++ show a ++
>                   ", Divisions: " ++ show d
>                   where (v, (a, d)) = apply st (0, 0)


> eval4 :: Term -> St Float
> eval4  = foldTerm fC fA fD
>   where
>   fC x = S (\ s -> (x, s))
>   fA st1 st2 = S (\ s ->
>                         let (x1, s1) = apply st1 s  in
>                         let (x2, s2) = apply st2 s1 in
>                         (x1 + x2, incA s2))
>   fD st1 st2 = S (\ s ->
>                         let (x1, s1) = apply st1 s  in
>                         let (x2, s2) = apply st2 s1 in
>                         (x1 / x2, incD s2))

(You might have reservations about the |let ... in; let ... in| style
where just |let ... ... in| would do.  I use the former because it is
also valid in Idris, while the latter isn't.)

As before, there is a lot of redundancy: |fA| and |fD| are almost identical,
and within the body of each of them there is the repeated pattern

<  \ s -> let (x1, s1) = apply st1 s in
<                        apply (f x1) s1

Moreover, there is a clear and present danger that the state won't be
threaded correctly.  Abstracting away the pattern helps with that too:
 
> ap           ::  St a -> (a -> St b) -> St b
> ap st f       =  S (\ s -> let (x1, s1) = apply st s in apply (f x1) s1)

> ret x         =  S (\ s -> (x, s))

> eval5        ::  Term -> St Float
> eval5         =  foldTerm fC fA fD
>   where
>   fC          =  ret

>   fA st1 st2  =  st1 `ap` (\ x1 ->
>                  st2 `ap` (\ x2 ->
>                  S (\ s -> (x1 + x2, incA s))))
                   
>   fD st1 st2  =  st1 `ap` (\ x1 ->
>                  st2 `ap` (\ x2 ->
>                  S (\ s ->  (x1 / x2, incD s))))

We are still handling the state explicitely, which is unavoidable (after
all, we are trying to count additions and divisions!), but we can make
it more uniform by introducing |get| and |put|:

> get :: St State
> get  = S (\ s -> (s, s))

> put :: State -> St ()
> put s = S (\ s' -> ((), s))

> stAct        ::  (State -> State) -> St ()
> stAct f       =  get `ap` (put . f)

> eval6        ::  Term -> St Float
> eval6         =  foldTerm fC fA fD
>   where
>   fC x        =  ret x

>   fA st1 st2  =  st1 `ap` (\ x1 ->
>                  st2 `ap` (\ x2 ->
>                  stAct incA `ap` (\ () ->
>                  ret (x1 + x2))))

>   fD st1 st2  =  st1 `ap` (\ x1 ->
>                  st2 `ap` (\ x2 ->
>                  stAct incD `ap` (\ () ->
>                  ret (x1 / x2))))

The |op|-|ap| pattern is encountered surprisingly often in programming, so
it has been abstracted out in the Haskell type class Monad:

< class Monad m where
<   return  ::  a    ->  m a
<   (>>=)   ::  m a  ->  (a  ->  m b)  ->  m b

subject to

<   return x  >>=  f       =  f x
<   f x       >>=  return  =  f x

<   (mx >>= f)  >>=  g     =  mx  >>=  (\ x -> f x >>= g)

Both |Exc| and |St| are instances of |Monad|:

> instance Monad Exc where
>   return       =  Return
>   (>>=)        =  op


> instance Monad St where
>   return a     =  S (\ s -> (a, s))
>   (S f) >>= g  =  S (\ s -> let (a, s1) = f s in apply (g a) s1)

and so is |Id|

> newtype Id a      =  Id a deriving (Show)

> instance Monad Id where
>   return          =  Id
>   (Id x) >>= f    =  f x

Now we can see that all the evaluators have (at least morally) the 
form

> evalM :: Monad m => Term -> m Float
> evalM  =  foldTerm fC fA fD
>           where
>           fC        =  return -- or something similar to return
>           fA m1 m2  =  m1 >>= (\ x1 ->
>                        m2 >>= (\ x2 ->
>                        -- ... some monad specific stuff ...
>                        return (x1 + x2)))

>           fD m1 m2  =  m1 >>= (\ x1 ->
>                        m2 >>= (\ x2 ->
>                        -- ... some monad specific stuff ...
>                        return (x1 / x2)))

The |m >>= \ x -> f x| is so frequent, that Haskell provides
a special notation for it:

<   m >>= \ x -> f x
<=>
<   do x <- m
<      return (f x)

Using |do|-notation, the general pattern becomes

> evalDo :: Monad m => Term -> m Float
> evalDo  =  foldTerm fC fA fD
>            where
>            fC        =  return -- or something similar to return    
>            fA m1 m2  =  do  x1 <- m1
>                             x2 <- m2
>                             -- ... some monad specific stuff ...
>                             return (x1 + x2)
                              
>            fD m1 m2  =  do  x1 <- m1
>                             x2 <- m2
>                             -- ... some monad specific stuff ...
>                             return (x1 / x2)

Reasoning with monads
---------------------

< do x1 <- evalDo (Add t1 t2)                do x1 <- evalDo (Add t1 t2)
<    x2 <- evalDo (Add t2 t1)      =            x2 <- evalDo (Add t2 t1)
<    return x1 == x2                            return True

<=>

< do x11 <- evalDo t1                        do x11 <- evalDo t1         
<    x12 <- evalDo t2                           x12 <- evalDo t2         
<    x1  <- return (x11 + x12)                  x1  <- return (x11 + x12)
<    x21 <- evalDo t2              =            x21 <- evalDo t2         
<    x22 <- evalDo t1                           x22 <- evalDo t1         
<    x2  <- return (x21 + x22)                  x2  <- return (x21 + x22)
<    return x1 == x2                            return True

What properties should the monad have in order for this to hold?

For example:

Commutativity:

< do x <- mx               do y <- my
<    y <- my        <=>       x <- mx
<    f x y                    f y x

Idempotence:

< do x1 <- mx              do x1 <- mx
<    x2 <- mx       <=>       x2 <- mx
<    f x1 x2                  f x1 x1

