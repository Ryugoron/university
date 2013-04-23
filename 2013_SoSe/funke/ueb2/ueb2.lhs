\ignore{
\begin{code}
import Control.Arrow
data Nat = O | S Nat deriving Show

fromNat :: Nat -> Integer
fromNat O       = 0
fromNat (S n)   = 1 + fromNat n

toNat :: Integer -> Nat
toNat 0         = O
toNat n         = S $ toNat (n-1)

paraNat :: ((Nat, a) -> a) -> a -> Nat -> a
paraNat _ e O     = e
paraNat f e (S n) = f (n, paraNat f e n)

foldNat' :: (a -> a) -> a -> Nat -> a
foldNat' _ e O     = e
foldNat' f e (S n) = foldNat' f (f e) n

foldNat :: (a -> a) -> a -> Nat -> a
foldNat _ e O      = e
foldNat f e (S n)  = f $ foldNat f e n

plus :: Nat -> Nat -> Nat
plus a b = foldNat S a b

times :: Nat -> Nat -> Nat
times a b = foldNat (plus a) O b

pow :: Nat -> Nat -> Nat
pow a b = foldNat (times a) (S O) b
\end{code}
}

\section*{Task 2}

Both solutions work essentially the same way. In \lstinline|fact2| we construct
the tupel explizitly. We compute the product of the current number
and of the previous computed factorial.

\begin{code}
fact1 :: Nat -> Nat
fact1 = paraNat ((uncurry  times) . (first S)) (S O) 

fact2 :: Nat -> Nat
fact2 = snd . (foldNat' (\(x,y) -> (S x, times (S x) y)) (O, S O))
\end{code}

Both functions perform a linear amount of multiplikations. In the second we construct
the number along the way, but this is only linear time.\\

In Haskell both are even in the place needed to store the functions.
Both store up to $n$ times functions.\\

In a strict language on the otherhand \lstinline|fact2| can execute the \lstinline|times| in
each iterations never holding more than one in the background. Therefor a constant stack of 
functions has to be stored.

\section*{Task 3}

\begin{code}
lengthL :: [a] -> Nat
lengthL = foldr (\_ b -> S b) O
\end{code}

We ignore the list element and just replace each (\lstinline|(:) a|) with a \lstinline|S|.
The empty list is replaced by a \lstinline|O|. We build up the successor for any element in the list
hence we computed the length.

\ignore{lengthL = foldr (S . (flip const)) O}

\begin{code}
sumL :: [Nat] -> Nat
sumL = foldr plus O

prodL :: [Nat] -> Nat
prodL = foldr times (S O)
\end{code}

Both these functions work the same. We gave it the neutral element of the operation and then
apply the operation between the elements of the list starting with the neutral element.\\

We apply all functions a linear number of times.

\section*{Task 4}

\ignore{
\begin{code}
paraList :: (b -> ([b],a) -> a) -> a -> [b] -> a
paraList f e []       = e
paraList f e (x:xs)   = f x (xs, paraList f e xs)
\end{code}}

\begin{code}
headL :: [a] -> Maybe a
headL =  paraList (\a _ -> Just a) Nothing
\end{code}
If the list is empty \lstinline|Nothing| is returned. In the other case \lstinline|paraList| gets the head as first input, hence
we simply return it packed in a Maybe.

\begin{code}
tailL :: [a] -> Maybe [a]
tailL = paraList (\ _ (as,_) -> Just as) Nothing
\end{code}

Tail essentially works the same way, just that paraList has put the tail of the list as second element in the second argument.\\

Both functions take constant time because we will throug away the recursive step and because Haskell is performing a outmost reduction
the recursion will not be executed.
