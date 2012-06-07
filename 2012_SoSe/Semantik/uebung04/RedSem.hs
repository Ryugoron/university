module RedSem where

import WhileParse
import Data.Map (Map)
import qualified Data.Map as Map


redT :: (T, (Map I Z, [K], [K])) -> (T, (Map I Z, [K], [K]))
redT ((Z z), t)           = ((Z z), t)
redT ((Id i), (s,e,a))     = 
    case Map.lookup i s of
        Just (z)  -> ((Z z), (s,e,a))
        Nothing     -> ((Id i), (s,e,a))
redT ((TApp v1 op v2), t) =
    let (r1, t')                = redT (v1, t)
        (r2, t'')               = redT (v2, t') in
            case (r1,r2) of
                (Z z1, Z z2)    -> ((Z (decodeOP op z1 z2)), t'')
                (lE, rE)        -> ((TApp lE op rE), t'')
redT (TRead, (s,((LiteralInteger z):e), a))  = ((Z z), (s,e,a))
redT (TRead, t)           = (TRead, t)
        

redB ((Literal b), t)           = ((Literal b), t)
redB ((Not b), t)               = 
    case redB (b, t) of
        ((Literal b'), t')   -> ((Literal (not b')), t')
        (b', t')             -> (b', t')
redB ((BApp b1 bop b2), t)      =
    let (r1, t')                = redT (b1, t)
        (r2, t'')               = redT (b2, t') in
            case (r1,r2) of
                (Z z1, Z z2)    -> ((Literal (decodeBOP bop z1 z2)), t'')
                (lE, rE)        -> ((BApp lE bop rE), t'')
redB (BRead, (s, ((LiteralBool b):e), a)) = ((Literal b), (s,e,a))
redB (BRead , t)                = (BRead, t)

red :: (C, (Map I Z, [K], [K])) -> (C, (Map I Z, [K], [K]))
red (Skip, t)                   = (Skip, t)
red ((Assign i t), (s,e,a))     = 
    let (v,(s',e',a')) = redT (t,(s,e,a)) in
        case v of
            (Z z)               -> (Skip, (Map.insert i z s', e', a'))
            _                   -> error ("Couldn't match " ++ (show t) ++" with Integer in "++(show i)++":="++(show t))
red ((Seq c1 c2), t)            =
    let (cr, t') = red (c1, t)      -- In our implementation could only be Skip
        (cr', t'') = red (c2, t')   -- Same again
    in
        case (cr,cr') of
            (Skip, Skip)         -> (Skip, t'')
            _                   -> error ("Couldn't match"++(show c1)++"; "++(show c2)++"to a real execution")
red ((If b c1 c2), t)            =
    case redB (b, t) of
        ((Literal True), t')    -> red (c1, t')
        ((Literal False), t')   -> red (c2, t')
        _                       -> error ((show b)++" in the expression "++(show (If b c1 c2))++"was no boolean")
red ((While b c), t)            =
    case redB (b,t) of
        ((Literal True), t')    -> 
            let (res, t'') = red (c, t') in
                case res of
                    (Skip)      -> red ((While b c), t'')
                    _           -> error ("Couldn't evaluate ("++(show c)++") in ("++(show (While b c))++")")
        ((Literal False), t'')   -> red (Skip, t'')
        _                       -> error ("Couldn't evaluate ("++(show b)++") in ("++(show (While b c))++")")
red ((BOut b), t)               =
    let (r, (s,e,a)) = redB (b,t) in
        case r of
            (Literal bool)  -> (Skip, (s,e,((LiteralBool bool):a)))
            _               -> error ("Type missmatch, expected bool in ("++(show b)++"), in ("++(show (BOut b))++")")
red ((TOut v), t)               =
    let (r, (s,e,a)) = redT (v,t) in
        case r of
            (Z z)           -> (Skip, (s,e,((LiteralInteger z):a)))
            _               -> error ("Type missmatch, expected bool in ("++(show v)++"), in ("++(show (TOut v))++")")


eval :: C -> [K] -> [K]
eval prog eing  = 
    case red (prog, (Map.empty, eing, [])) of
        (Skip, (_,_,aus))   -> aus
        _                   -> error ("The programm wasn't evaluated complete")
