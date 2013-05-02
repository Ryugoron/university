\ignore{
\begin{code}
module Field
   ( Complex(..)
   , ZRing(..)
   , Field(..)
   , znRoot
   , rootOfUnityC
   ) where
\end{code}}



\begin{code}
class Field a where
    add :: a -> a -> a
    add x y     = sub x (addInv y)

    sub :: a -> a -> a
    sub x y     = add x (addInv y)

    mult :: a -> a -> a
    mult x y    = divi x (multInv y)

    divi :: a -> a -> a
    divi x y     = mult x (multInv y)

    addInv :: a -> a
    addInv x    = sub (nAdd x) x

    multInv :: a -> a
    multInv x   = divi (nMult x) x 

    fromInt :: a -> Int -> a
\end{code}


Basis because we can't extract the basis of Z_n without dependent type systems.

>    nAdd :: a -> a
>    nMult :: a -> a

>data Complex = C Double Double

>instance Field Complex where
>   add (C a b) (C c d)     = C (a+c) (b+d)
>   sub (C a b) (C c d)     = C (a-c) (b-d)
>   mult (C a b) (C c d)    = C (a*c - b*d) (b*c + a*d)
>   divi (C a b) (C c d)    = C ((a*c + b*d)/(c*c + d*d)) ((b*c - a*d)/(c*c + d*d))
>   nAdd _                  = C 0 0
>   nMult _                 = C 1 0
>   fromInt _ n               = C (fromIntegral $ toInteger n) 0

>rootOfUnityC :: Double -> Complex
>rootOfUnityC n         = C (cos (2*pi/n)) (sin (2*pi/n))

>instance Show Complex where
>   show (C a b)    = "("++(show a)++" + "++(show b)++"i)"


>data ZRing  =   Z Integer Integer

We assume the bases to equal, therfor we only consider the first 
basis and take the second one as number to the base of the first one.

>instance Field ZRing where
>   add (Z a n) (Z c _)     = Z (mod (a + c) n) n
>   sub (Z a n) (Z b _)     = Z (mod (a - b) n) n
>   mult (Z a n) (Z b _)    = Z (mod (a * b) n) n
>   addInv (Z a n)          = Z (mod (n - a) n) n
>   multInv (Z a n)         = Z (fastInverse n 1 a (n-2)) n
>   nAdd (Z _ n)            = Z 0 n
>   nMult (Z _ n)           = Z 1 n
>   fromInt (Z _ m) n       = Z (toInteger n) (toInteger m)

>isPrim :: Integer -> Bool
>isPrim n  = foldr (\y x -> x && not (mod n y == 0)) True [2..(div n 2)]

znRoot returns all Primnumbers N for a given 'c' satisfying N = c * 2^r + 1.

>znRoot :: Integer -> [(Integer,Integer)]
>znRoot c = [(r,c*2^r + 1) | r <- [1..] , isPrim (c*2^r + 1)]

>bruteForceRoot 
>   :: Integer      -- Base n
>   -> Integer      -- Akt c
>   -> Integer      -- Akt r
>   -> Integer      -- Solution r to n = c * 2^r + 1
>bruteForceRoot n c r
>   | c > n     = error "This "

>instance Show ZRing where
>   show (Z a _)            = show a

Copmutes the Inverse in Z_n (if n is prim) with
modular exponentiation and the law of euler.

>fastInverse 
>   :: Integer      -- Base n
>   -> Integer      -- accu b
>   -> Integer      -- a
>   -> Integer      -- e
>   -> Integer      -- b * a^e mod n
>fastInverse n b a 0   = b
>fastInverse n b a e
>   | mod e 2 == 0  = fastInverse n (mod (b*b) n) a (div e 2)
>   | otherwise     = fastInverse n (mod (b*a) n) a (e - 1)
