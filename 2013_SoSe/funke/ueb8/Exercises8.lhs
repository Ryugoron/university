> import Lecture7summary

Exercises 8
-----------

1.  Define an evaluator that returns a minimal trace of the evaluation, by
    completing the following code (get rid of all the |undefined|s):

> newtype Trace a      =  T (String, a)

> fstT                ::  Trace a -> String
> fstT  (T (s,_))      =  s

> sndT                ::  Trace a -> a
> sndT  (T (_,v))      = v

We do not want to show the trace, but only the value. If the Trace is wanted
you may call fstT.show

> instance Show a => Show (Trace a) where
>   show              = show.sndT 

> eval                ::  Term -> Trace Float
> eval                 =  foldTerm fC fA fD
>           where
>           fC x       =  T (show x ++ "\n", x)
>           fA t1 t2   =  T (fstT t1 ++ fstT t2 ++ show x ++ "\n", x)
>                         where
>                           x = ((sndT t1) + (sndT t2))::Float
>           fD t1 t2   =  T (fstT t1 ++ fstT t2 ++ help ++ "\n", x)
>                         where
>                           x = (sndT t1) / (sndT t2)
>                           help = if sndT t2 == 0 then "Division by zero error => infty" else show x

2.  Give a |Monad| instance declaration for |Trace| and rewrite
    the evaluator in monadic form.

> instance Monad Trace where
>   return x           =  T ("",x)
>   t >>= f    =  let T (s', v') = (f . sndT) t in
>                             T ((fstT t) ++ s', v')

Fehlerkram fehlt noch

> eval'               ::  Term -> Trace Float
> eval'                =  foldTerm fC fA fD
>           where
>           fC         =  \v -> T (show v ++ "\n",v)
>           fA t1 t2   =  do
>                           v <- t1
>                           v' <- t2
>                           return (v+v')
>           fD t1 t2   =  do 
>                           v <- t1
>                           v' <- t2
>                           return (v/v')

    Show that the |return| and |>>=| you have defined satisfy the monad 
    laws.

Left Identity: (return a >>= f = f a)
	return a >>= f 
= {def. f, (s', v') = return a}
	T( s ++ s', v ++ v')
=  {def. return, (s, v) = ("", a) = return a}
	T (""++s', v')
= {def. (++) ""++s' = s'}
	T (s', v')
= {def. reverse. f a}
	f a

Right Identity: (m >>= return <=> m, where m = T (s,v))
	(T (s,v)) >>= return
= {def. >>=}
	let T (s', v') =  return v in T (s++s', v')
= {def. return}
	let T (s', v') = ("",v) in T (s ++ s', v')
= {plug in let}
	T ( s ++ "", v)
= {lemma. s++"" = s, (was shown previously in exercise)}
	T (s,v)

Associativity: (m >>= f) >>= g <=> m >>= (\x -> fx >>= g), sei m = T(s,v)
=> 
= {def. >>= g}
	let (T (s', v') = g (sndT (m >>= f)) in T ((fstT (m >>= f)) ++ s', v')
= {def >>= f}
	let (T (s', v') = g (sndT (let (s'', v'') = f (sndT m) in T ((fstT m)++s'', v''))) in T ((fstT (let T (s'', v'') = f (sndT m) in T((fstT m) ++ s'', v'')) ++ s', v')
= {def fstT, sndT}
	let (T (s', v') = g (sndT (let (s'', v'') = f v in T (s ++ s'', v''))) in T ((fstT (let T (s'', v'') = f s in T (s ++ s'', v'')))))++ s', v')
= {def apply let}
	let (T (s', v') = g v'') in T ((s ++ s'')++s', v')
	
<=
	m >>= (\x -> fx >>= g)
= {def >>=}
	let T (s', v') = (\x -> fx >>= g) (sndT m) in (fstT m ++ s', v')
= {def. sndT}
	let T (s', v') = (\x -> fx >>=g) v in (s ++ s', v')
= {apply \x}
	let T (s', v') = (fv >>= g) in (s ++ s', v')
= {def. >>=}
	let T (s', v') = (let T (s'', v'') = g (sndT f v)) in (fstT (f v) ++ s'', v'') in (s ++ s', v')
= {f v = (s''', v''')}
	let T (s', v') = (let T (s'', v'') = g v''') in (s''' ++ s'', v'') in (s ++ s'), v')
= {bla}
3.  Many recursive types form monads.  For example, if |F| is an arbitrary
    functor such as |Id|, |[]|, or |STree|, then

> type F a             =  [a]  --  This is just an example for testing
>                              --  purposes, but any other functor would 
>                              --  do just as well                             
> data G a             =  C1 a  |  C2 (F (G a))

G A = {C1 a, C2 [], C2 [C1 a], C2 [C1 a, C1 a], ...,  C2 [C2 [C1 a]] ...

    forms a monad.  Show that this is the case by completing the following
    instance declaration:

> instance Monad G where
>   return             =  C1
>   (C1 a)  >>=  f     =  f a
>   (C2 x)  >>=  f     =  C2 $ fmap (>>=f) x 

    Show that the |return| and |>>=| you have defined fulfill the monad
    laws.

< (return a >>= f = f a)

  return a >>= f
= C1 a >>= f
= f a

< f x       >>=  return  =  f x

case 1: f x = C1 a
  f x >>= return
= C1 a >>= return
= return a
= C1 a = f x

case 2: f x = C2 z
  f x >>= return
= C2 z >>= return
*= C2 $ fmap (>>= return) z
= C2 $ z
= f x

etc. i hope
