module OpSem where

import Data.Map
import qualified Data.Map as Map
import WhileParse
import qualified WhileParse as Wskea

class (Show out, Eq out) => Ausgabe out where
    getI :: Ausgabe out -> out

class (Show out, Eq out, Ausgabe m) => AbsMaschine (m out) where
    delta :: (m out) -> (m out)
    fix :: (m out) -> (m out)
    o :: (m out) -> out

data WSKEA = W Wskea.WSKEA

instance Ausgabe (WSKEA Wskea.K) where
    getI (w,s,k,e,a) = a

instance AbsMaschine WSKEA where
    delta prog  = Wskea.delta prog
    fix prog =
        case delta prog of
            prog    -> prog
            prog'   -> fix prog'
    o prog = getI.fix $ prog
