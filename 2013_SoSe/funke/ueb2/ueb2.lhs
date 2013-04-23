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

\begin{code}
fact1 :: Nat -> Nat
fact1 = paraNat ((uncurry  times) . (first S)) (S O) 

fact2 :: Nat -> Nat
fact2 = snd . (foldNat' (\(x,y) -> (S x, times (S x) y)) (O, S O))
\end{code}

\section*{Task 3}

\begin{code}
lengthL :: [a] -> Nat
lengthL = foldr (\a b -> S b) O
\end{code}

\ignore{lengthL = foldr (S . (flip const)) O}

\begin{code}
sumL :: [Nat] -> Nat
sumL = foldr plus O

prodL :: [Nat] -> Nat
prodL = foldr times (S O)
\end{code}

\section*{Task 4}

\begin{code}
paraList :: (b -> ([b],a) -> a) -> a -> [b] -> a
paraList f e []       = e
paraList f e (x:xs)   = f x (xs, paraList f e xs)

headL :: [a] -> Maybe a
headL =  paraList (\a _ -> Just a) Nothing

tailL :: [a] -> Maybe [a]
tailL = paraList (\ _ (as,_) -> Just as) Nothing
\end{code}
