data Nat = O | S Nat deriving Show

foldNat :: (a -> a) -> a -> Nat -> a
foldNat _ e O      = e
foldNat f e (S n)  = f $ foldNat f e n

mPred :: Nat -> Maybe Nat
mPred = foldNat (maybe (Just O) (Just . S)) Nothing
