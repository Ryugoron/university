\ignore{
\begin{code}
module FFT
   ( fft
   , dft
   , convulate
   , expandTo
   ) where

--Import the definition for Fields

import Field
\end{code}}

Für die Fouriertansformation splitten wir in jedem Schritt die List der Koeffizienten,
berechnen die transformation und kombinieren sie danach, wie im Algorithmus der VL.

\begin{code}
fft :: (Field a) 
   => a        -- w Root of unity 
   -> [a]      -- a Coeffizient Vector
   -> [a]      -- Solution V(w) a, V - Vandormond of w
fft _ []  = error $ "No input to fft"
fft _ [a] = [a]
fft w as  = 
   let
       (ag, au)    = split as
       (ger, unger)      = (fft (mult w w) ag, fft (mult w w) au)
       ws          = iterate (mult w) (nMult w)
   in
       (zipWith (add) ger $ zipWith (mult) ws unger) ++ (zipWith (sub) ger $ zipWith (mult) ws unger)
\end{code}

\ignore{
\begin{code}
a = expandTo 8 (Z 0 17) [Z 2 17, Z 3 17, Z 5 17]
b = expandTo 8 (Z 0 17) [Z 3 17, Z 2 17, Z 7 17]
w = Z 2 17
\end{code}
}

\ignore{
\begin{code}
split :: [a] -> ([a],[a])
split []                = ([],[])
split [a]               = ([a], [])
split (a:b:xs)          = (\(as,bs) -> (a:as,b:bs)) $ split xs
\end{code}}

Für die Umkehrung der schnellen Fouriertransformation invertieren wir die Einheitswurzel
führen die Transformation aus und teilen dann durch $n+1$ (was in unserem Fall die Länge
der Koeffizienten List ist).

\begin{code}
dft :: (Field a)
   => a        -- w Root of unity
   -> [a]      -- a Vector of Evaluations at points of the Root of Unity
   -> [a]      -- Solution (1/(n+1) V(w))^-1 a, where V(w) is the Vandermondmatrix of w
dft w xs           = map (\x -> divi x (fromInt x (length xs))) $ fft (multInv w) xs
\end{code}

Um schließlich die Faltung der Vektoren zu berechnen führen wir die Fouriertransformation aus,
multiplizieren die Einträge Komponentenweise und kehren die Transformation dann um.

\begin{code}
convulate :: (Field a)
   =>  a       -- w Root of unity
   ->  [a]     -- a Vector of coeffizients, the size has to be matched to the solution rank
   ->  [a]     -- b Vector of coeffizeints, the size has to be matched to the solution rank
   ->  [a]     -- Multiplication of the vectors a (x) b = (V(w))^-1 ( (V(w)a) * (V(w)b))
convulate _ [] _   = []
convulate _ _ []   = []
convulate w as bs    = dft w $ zipWith (mult) (fft w as) (fft w bs)
\end{code}

\ignore{
\begin{code}
expandTo :: (Field a) => Integer -> a -> [a] -> [a]
expandTo n e []        = take (fromInteger n) $ repeat (nAdd e)
expandTo n e (x:xs)    = x : expandTo (n-1) e xs
\end{code}}
