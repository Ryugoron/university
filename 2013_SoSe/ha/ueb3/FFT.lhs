
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
>       (ag, au)    = split as ([],[])
>       (v, u)      = (fft (mult w w) ag, fft (mult w w) au)
>       ws          = iterate (mult w) w
>   in
>       (zipWith (add) u $ zipWith (mult) ws v) ++ (zipWith (sub) u $ zipWith (mult) ws v)

>split :: [a] -> ([a],[a]) -> ([a],[a])
>split [] x                = x
>split [a] (as,bs)         = (a:as, bs)
>split (a:b:xs) (as,bs)    = split xs (a:as, b:bs)

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

