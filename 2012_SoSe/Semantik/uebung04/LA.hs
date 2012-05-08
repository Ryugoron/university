module LA where

type W      = Bool
data LOP    = AND | OR | NOT deriving (Show,Eq)
data LA     = Value W | App LA LOP LA | Not LA  deriving Eq

data M      = MLA LA | FUN LOP deriving (Eq,Show)

instance Show LA where
    show (Value w)          = show w
    show (App t1 AND t2)    = "("++(show t1)++"&&"++(show t2)++")"
    show (App t1 OR t2)     = "("++(show t1)++"||"++(show t2)++")"
    show (Not t)            = "not ("++(show t)++")"

test1, test2 :: LA
test1   = App (Value True) AND (App (Value False) OR (Value True))
test2   = Not (App (Value True) AND (App (Value True) AND (App (Value False) OR (Value False))))


-- Abstrakte Maschinenen Part

delta :: ([W],[M],[String]) -> ([W],[M],[String])
delta (s,((MLA (Value b)):k), a)        = ((b:s),k,a)
delta (s,((MLA (App t1 op t2)):k),a)    = (s, ((MLA t1):(MLA t2):(FUN op):k),a)
delta (s,((MLA (Not t)):k),a)           = (s, ((MLA t):(FUN NOT):k),a)
delta ((b:s),((FUN NOT):k),a)       
    = (((not b):s),k,(("not ("++(show b)++") = "++(show.not $ b)):a))
delta ((b2:b1:s),((FUN AND):k),a)   
    = (((b1 && b2):s),k,(((show b1)++" && "++(show b2)++" = "++(show (b1&&b2))):a))
delta ((b2:b1:s),((FUN OR):k),a)   
    = (((b1 || b2):s),k,(((show b1)++" || "++(show b2)++" = "++(show (b1||b2))):a))
delta k                                 = k

anfang :: LA -> ([W],[M],[String])
anfang prog = ([],[(MLA prog)],[])

fix :: ([W],[M],[String]) -> ([W],[M],[String])
fix z   =
    let z' = delta z in
        if z == z' then
            z
        else
            fix z'

o :: ([W],[M],[String]) -> [String]
o z = 
    let (_,_,aus) = fix z in
        aus

-- Reduktionssemantik

red :: (LA, [String]) -> (LA, [String])   -- Programm und Ausgabe
red (Value b, a)    = (Value b, a)
red (App t1 AND t2, a')  =
    let (v1,a'')    = red (t1,a')
        (v2,a)      = red (t2,a'')
    in
        case (v1,v2) of
            (Value w1, Value w2)    
                -> (Value (w1 && w2), (((show w1)++" && "++(show w2)++" = "++(show (w1&&w2))):a))
            _   -> error "no value returned"
red (App t1 OR t2, a')   =
    let (v1,a'')    = red (t1,a')
        (v2,a)      = red (t2,a'')
    in
        case (v1,v2) of
            (Value w1, Value w2)
                -> (Value (w1 || w2), (((show w1)++" || "++(show w2)++" = "++(show (w1||w2))):a))
            _   -> error "no value returned"
red (Not t1, a')    =
    let (v1,a)  = red (t1,a') in
        case v1 of
            (Value w)   -> (Value (not w),(("not ("++(show w)++") = "++(show.not $ w)):a))
            _           -> error "no value returned"

eval :: LA -> [String]
eval z = snd.red $ (z,[])
