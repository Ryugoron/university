module OpSem2 where

import Data.Map (Map)
import qualified Data.Map as Map
-- import WhileParse
import qualified WhileParse as Wskea

class (Eq m) => Maschine m where
    delta :: m -> m
    fix :: m -> m
    fix z   =
        let z' = delta z in
            if z == z' then z else fix z'

    o :: m -> String

data WM = W Wskea.WSKEA deriving (Eq, Show)

instance Maschine WM where
    delta (W w) = W (Wskea.delta w)
    o w =
        case fix w of
            (W (w,s,k,e,a)) -> show a
