
>module FFT
>   ( fft
>   , dft
>   , convulate
>   ) where


Import the definition for Fields

>import Field

>fft :: (Field a) 
>   => a        -- w Root of unity 
>   -> [a]      -- a Coeffizient Vector
>   -> [a]      -- Solution V(w) a, V - Vandormond of w
>fft _ []  = error $ "No input to fft"
>fft _ [a] = [a]
>fft w as  = 
>   let
>       (ag, au)    = split as
>       (ger, unger)      = (fft (mult w w) ag, fft (mult w w) au)
>       ws          = iterate (mult w) (nMult w)
>   in
>       (zipWith (add) ger $ zipWith (mult) ws unger) ++ (zipWith (sub) ger $ zipWith (mult) ws unger)

>a = expandTo 8 (Z 0 17) [Z 2 17, Z 3 17, Z 5 17]
>b = expandTo 8 (Z 0 17) [Z 3 17, Z 2 17, Z 7 17]
>w = Z 2 17

>split :: [a] -> ([a],[a])
>split []                = ([],[])
>split [a]               = ([a], [])
>split (a:b:xs)          = (\(as,bs) -> (a:as,b:bs)) $ split xs

>dft :: (Field a)
>   => a        -- w Root of unity
>   -> [a]      -- a Vector of Evaluations at points of the Root of Unity
>   -> [a]      -- Solution (1/(n+1) V(w))^-1 a, where V(w) is the Vandermondmatrix of w
>dft w xs           = map (\x -> divi x (fromInt x (length xs))) $ fft (multInv w) xs

>convulate :: (Field a)
>   =>  a       -- w Root of unity
>   ->  [a]     -- a Vector of coeffizients, the size has to be matched to the solution rank
>   ->  [a]     -- b Vector of coeffizeints, the size has to be matched to the solution rank
>   ->  [a]     -- Multiplication of the vectors a (x) b = (V(w))^-1 ( (V(w)a) * (V(w)b))
>convulate _ [] _   = []
>convulate _ _ []   = []
>convulate w as bs    = dft w $ zipWith (mult) (fft w as) (fft w bs)

>expandTo :: (Field a) => Integer -> a -> [a] -> [a]
>expandTo n e []        = take (fromInteger n) $ repeat (nAdd e)
>expandTo n e (x:xs)    = x : expandTo (n-1) e xs

