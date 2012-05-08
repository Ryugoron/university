module WhileParse where

import Data.Map (Map)
import qualified Data.Map as Map

data T = Z Z
       | Id I
       | TApp T OP T
       | TRead
        

data B = Literal W
       | Not B
       | BApp T BOP T
       | BRead
        

data C = Skip
       | Assign I T
       | Seq C C
       | If B C C
       | While B C
       | BOut B
       | TOut T
        deriving Show

type P = C

type Z = Int
type W = Bool
type I = String
data K = LiteralInteger Z | LiteralBool W 
data OP = Plus | Minus | Mult | Div | Mod 
data BOP = Eq | Lt | Gt | Lte | Gte | Neq 

instance Show K where
    show (LiteralInteger i) = show i
    show (LiteralBool b)    = show b

instance Show OP where
    show = nameOp

instance Show BOP where
    show = nameBop

instance Show T where
    show (Z z)              = show z
    show (Id i)              = i
    show (TApp t1 op t2)    = '(':(show t1)++(nameOp op)++(show t2)++")"
    show (TRead)            = "'read'"

instance Show B where
    show (Literal b)        = show b
    show (Not b)            = "not ("++(show b)++")"
    show (BApp t1 bop t2)   = '(':(show t1) ++ (nameBop bop) ++ (show t2)++")"
    show BRead              = "'read'"

data Con = WhileC | IfC | NotC | AssignC I | BOutC | TOutC deriving Show

data PK = WhileProgramm C | ControlOp OP | ControlBop BOP | ControlC Con | ControlB B | ControlT T deriving Show

type WSKEA = ([K],Map I Z, [PK], [K], [K])

anfang :: P ->  [K] -> WSKEA
anfang program eingabe = ([], Map.empty , [(WhileProgramm program)], eingabe, [])

delta :: WSKEA -> WSKEA
delta (w,s,[],e,a) = (w,s,[],e,a)
-- ControlC
delta (((LiteralBool True):w), s, ((ControlC WhileC):b:p:k),e, a) 		= (w, s, (p:b:(ControlC WhileC):b:p:k), e, a)
delta (((LiteralBool False):w), s, ((ControlC WhileC):_:_:k), e, a)		= (w, s, k, e, a)
delta (((LiteralBool True):w), s, ((ControlC IfC):p1:_:k), e, a)		= (w, s, (p1:k), e, a)
delta (((LiteralBool False):w), s, ((ControlC IfC):_:p2:k), e, a)		= (w, s, (p2:k), e ,a)
delta (((LiteralBool b):w), s, ((ControlC NotC):k), e, a)				= (((LiteralBool (not b)):w), s, k, e, a)
delta (((LiteralInteger v):w), s, ((ControlC (AssignC id)):k), e, a)	= (w, Map.insert id v s, k, e, a)
delta (((LiteralInteger v):w), s, ((ControlC TOutC):k), e, a)			= (w, s, k, e, ((LiteralInteger v):a))
delta (((LiteralBool b):w), s, ((ControlC BOutC):k), e, a)				= (w, s, k, e, ((LiteralBool b):a))
-- ControlBop und Op
delta (((LiteralInteger i1):(LiteralInteger i2):w), s, ((ControlBop bop):k), e, a)
	= (((LiteralBool (decodeBOP bop i2 i1)):w), s, k, e, a)
delta (((LiteralInteger i1):(LiteralInteger i2):w), s, ((ControlOp op):k), e, a)
	= (((LiteralInteger (decodeOP op i2 i1)):w), s, k, e, a)
-- ControlB Boolauswertung
delta (w,s,((ControlB (Literal b)):k),e,a)
	= (((LiteralBool b):w),s,k,e,a)
delta (w,s,((ControlB (Not b)):k), e, a)
	= (w, s, ((ControlB b):(ControlC NotC):k), e, a)
delta (w,s,((ControlB (BApp t1 bop t2)):k),e,a)
	= (w, s, ((ControlT t1):(ControlT t2):(ControlBop bop):k), e, a)
delta (w, s, ((ControlB BRead):k), ((LiteralBool b):e), a)
	= (((LiteralBool b):e), s, k, e, a)
-- ControlT Termauswertung
delta (w, s, ((ControlT (Z z)):k), e, a)
	= (((LiteralInteger z):w), s, k ,e ,a)
delta (w, s, ((ControlT (Id i)):k), e, a) =
	let v = Map.lookup i s in
		case v of
			(Just value)	-> (((LiteralInteger value):w), s, k, e, a)
			_				-> error "No transition defined"
delta (w, s, ((ControlT (TApp t1 op t2)):k), e, a)
	= (w, s, ((ControlT t1):(ControlT t2):(ControlOp op):k), e, a)
delta (w, s, ((ControlT (TRead)):k), ((LiteralInteger i):e), a)
	= (((LiteralInteger i):w), s , k, e, a)
-- WhileProgramm
delta (w, s, ((WhileProgramm Skip):k), e, a)
	= (w, s, k, e, a)
delta (w, s, ((WhileProgramm (Assign i t)):k), e, a)
	= (w, s, ((ControlT t):(ControlC (AssignC i)):k), e, a)
delta (w, s, ((WhileProgramm (Seq c1 c2)):k), e, a)
	= (w, s, ((WhileProgramm c1):(WhileProgramm c2):k), e, a)
delta (w, s, ((WhileProgramm (While b c)):k), e, a)
	= (w, s, ((ControlB b):(ControlC WhileC):(ControlB b):(WhileProgramm c):k), e, a)
delta (w, s, ((WhileProgramm (If b c1 c2)):k), e, a)
	= (w, s, ((ControlB b):(ControlC IfC):(WhileProgramm c1):(WhileProgramm c2):k), e, a)
delta (w, s, ((WhileProgramm (BOut b)):k), e, a)
	= (w, s, ((ControlB b):(ControlC BOutC):k), e, a)
delta (w, s, ((WhileProgramm (TOut t)):k), e, a)
	= (w, s, ((ControlT t):(ControlC TOutC):k), e, a)
-- undefined stuff
delta _ = error "No transition defined"



decodeOP Plus v1 v2     = v1 + v2
decodeOP Minus v1 v2    = v1 - v2
decodeOP Mult v1 v2     = v1 * v2
decodeOP Div v1 v2      = v1 `div` v2
decodeOP Mod v1 v2      = v1 `mod` v2

decodeBOP Eq v1 v2    = v1 == v2
decodeBOP Lt v1 v2    = v1 < v2
decodeBOP Gt v1 v2    = v1 > v2
decodeBOP Lte v1 v2   = v1 <= v2
decodeBOP Gte v1 v2   = v1 >= v2
decodeBOP Neq v1 v2   = v1 /= v2

nameOp :: OP -> String
nameOp Plus	  = "+"
nameOp Minus  = "-"
nameOp Mult   = "*"
nameOp Div    = "/"
nameOp Mod    = "mod"

nameBop :: BOP -> String
nameBop Eq    = "="
nameBop Lt    = "<"
nameBop Gt    = ">"
nameBop Lte   = "<="
nameBop Gte   = ">="
nameBop Neq   = "/="

divprog :: P
divprog = Seq 
            (While
                (BApp (Id "x") Gte (Id "y"))
                   (Seq
                    (Assign ("g") (TApp (Id "g") Plus (Z 1)))
                    (Assign ("x") (TApp (Id "x") Minus (Id "y")))))
            (TOut (Id "g"))

divTest :: P
divTest = Seq
            (Assign "x" TRead)
            (Seq
            (Assign "y" TRead)
            (Seq
            (Assign "g" (Z 0))
            divprog))
