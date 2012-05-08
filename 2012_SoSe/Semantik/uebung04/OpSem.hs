module OpSem where

import Data.Map
import qualified Data.Map as Map
import WhileParse
import qualified WhileParse as Wskea

class Operation m n where
    delta :: m -> m
    o :: m -> n

instance Operation Wskea.WSKEA K where
    delta zustand       = Wskea.delta zustand
    o (_,_,[],out,_)    = out
    o                   = o.delta
