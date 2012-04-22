\documentclass[11pt,a4paper,ngerman]{article}
\usepackage[bottom=2.5cm,top=2.5cm]{geometry} 
\usepackage{babel}
\usepackage[utf8]{inputenc} 
\usepackage[T1]{fontenc} 
\usepackage{ae} 
\usepackage{amssymb} 
\usepackage{amsmath} 
\usepackage{graphicx}
\usepackage{fancyhdr}
\usepackage{fancyref}
\usepackage{listings}
\usepackage{xcolor}
\usepackage{paralist}

\lstloadlanguages{Haskell}
\lstnewenvironment{code}
    {\lstset{}%
      \csname lst@SetFirstLabel\endcsname}
    {\csname lst@SaveFirstLabel\endcsname}
    \lstset{
      basicstyle=\small\ttfamily,
      flexiblecolumns=false,
      basewidth={0.5em,0.45em},
      literate={+}{{$+$}}1 {/}{{$/$}}1 {*}{{$*$}}1 {=}{{$=$}}1
               {>}{{$>$}}1 {<}{{$<$}}1 {\\}{{$\lambda$}}1
               {\\\\}{{\char`\\\char`\\}}1
               {->}{{$\rightarrow$}}2 {>=}{{$\geq$}}2 {<-}{{$\leftarrow$}}2
               {<=}{{$\leq$}}2 {=>}{{$\Rightarrow$}}2 
               {\ .}{{$\circ$}}2 {\ .\ }{{$\circ$}}2
               {>>}{{>>}}2 {>>=}{{>>=}}2
               {|}{{$\mid$}}1               
    }

\long\def\ignore#1{}

%\usepackage[pdftex, bookmarks=false, pdfstartview={FitH}, linkbordercolor=white]{hyperref}
\usepackage{fancyhdr}
\pagestyle{fancy}
\fancyhead[C]{Semantik von Programmiersprachen}
\fancyhead[L]{Übung Nr. 2}
\fancyhead[R]{SoSe 2012}
\fancyfoot{}
\fancyfoot[L]{}
\fancyfoot[C]{\thepage / \pageref{LastPage}}
\renewcommand{\footrulewidth}{0.5pt}
\renewcommand{\headrulewidth}{0.5pt}
\setlength{\parindent}{0pt} 
\setlength{\headheight}{0pt}

\author{Tutor: Ansgar Schneider}
\date{}
\title{Max Wisniewski , Alexander Steen}

\begin{document}

\lstset{language=Pascal, basicstyle=\ttfamily\fontsize{10pt}{10pt}\selectfont\upshape, commentstyle=\rmfamily\slshape, keywordstyle=\rmfamily\bfseries, breaklines=true, frame=single, xleftmargin=3mm, xrightmargin=3mm, tabsize=2}

\maketitle
\thispagestyle{fancy}

\ignore{
\begin{code}
module WhileParse where

import Data.Map (Map)
import qualified Data.Map as Map
\end{code}
}
%% ------------------------------------------------------
%%                     AUFGABE 1
%% ------------------------------------------------------

\section*{Aufgabe 1}
Spezifizieren Sie einen geeigneten Datentyp in Haskell oder Java, zur Be-
handlung der abstrakten Syntax der Sprache WHILE.

Elementare Einheiten:
\begin{code}
type Z = Int
type W = Bool
type I = String
data K = LiteralInteger Z | LiteralBool W 
data OP = Plus | Minus | Mult | Div | Mod 
data BOP = Eq | Lt | Gt | Lte | Gte | Neq 
\end{code}

Induktiv aufgebaute Einheiten:
\begin{code}
data T = Z Z
       | I I
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
\end{code}

\ignore{
\begin{code}
instance Show K where
    show (LiteralInteger i) = show i
    show (LiteralBool b)    = show b

instance Show OP where
    show = nameOP

instance Show BOP where
    show = nameBOP

instance Show T where
    show (Z z)              = show z
    show (I i)              = i
    show (TApp t1 op t2)    = '(':(show t1)++(nameOp op)++(show t2)++")"
    show (TRead)            = "'read'"

instance Show B where
    show (Literal b)        = show b
    show (Not b)            = "not ("++(show b)++")"
    show (BApp t1 bop t2)   = '(':(show t1) ++ (nameBop bop) ++ (show t2)++")"
    show BRead              = "'read'"


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
\end{code}
}

%% ------------------------------------------------------
%%                     AUFGABE 2
%% ------------------------------------------------------

\section*{Aufgabe 2}
Vereinbaren Sie das Divisionsbeispiel aus der Vorlesung als Konstante divprog
unter Verwendung der in Aufgabe 1 vereinbarten Datentypen.

\begin{code}
divprog :: P
divprog = Seq 
            (While
                (BApp (I "x") Gte (I "y"))
                   (Seq
                    (Assign ("g") (TApp (I "g") Plus (Z 1)))
                    (Assign ("x") (TApp (I "x") Minus (I "y")))))
            (TOut (I "g"))
\end{code}

% -----------------------------------------------------------
%			AUFGABE 3
% -----------------------------------------------------------

\section*{Aufgabe 3}
Definieren Sie je eine Funktion zur Berechnung der Werte von Termen,
bzw. booleschen Termen, die die aktuelle Speicherbelegung und die aktuelle
Eingabe als Parameter erhält.\\

Zur Auswertung bekommt von T bekomt evalT ein Wörterbuch von Identifier nach Zahl.
Dazu bekommt er eine List von Eingabewerten. Das selbe gilt für den boolschen Term.
Zurück kommt der Wert, den der Term ergeben sollte und die Restliste von Eingaben.

\begin{code}
		
evalT :: T                       -- arithmetischer Term
        -> Map I Z               -- Variablenbelegung
		-> [K]                   -- Liste von Eingaben
		-> (Z, [K])              -- Tupel (Rueckgabewert (Zahl), Input)
evalT (Z v) _ e       = (v,e)
evalT (I v) m e       =
	let mvalue = Map.lookup v m in
		case mvalue of
			Just value	->  (value, e)
			Nothing 	-> error "No Variable was found"
evalT (TApp t1 op t2) m e =
	let (v1, e1) = evalT t1 m e in
		let (v2, e2) = evalT t2 m e1 in
			(decodeOP op v1 v2, e2)
evalT (TRead) _ ((LiteralInteger e):es) = (e,es)
evalT _ _ _			   = error "Not enough or wrong  Input"

		
evalB :: B                       -- boolscher Term
         -> Map I Z              -- Variablenbelegung
		 -> [K]                  -- List von Eingaben
		 -> (W, [K])             -- Rueckgabewert (Bool)
evalB (Literal b) _ e       = (b,e)
evalB (Not b) m e           = 
    let (rBool, e1) = evalB b m e in
        (not rBool, e1)
evalB (BApp t1 bop t2) m e  =
	let (b1, e1) = evalT t1 m e in
		let (b2, e2) = evalT t2 m e1 in
			(decodeBOP bop b1 b2, e2)
evalB BRead m ((LiteralBool b):es)        = (b,es)
evalB _ _ _                 = error "Not enough or wrong Input."
		 
		 
decodeOP :: OP -> Z -> Z -> Z
decodeOP Plus v1 v2     = v1 + v2
decodeOP Minus v1 v2    = v1 - v2
decodeOP Mult v1 v2     = v1 * v2
decodeOP Div v1 v2      = v1 `div` v2
decodeOP Mod v1 v2      = v1 `mod` v2

decodeBOP :: BOP -> Z -> Z -> W
decodeBOP Eq v1 v2    = v1 == v2
decodeBOP Lt v1 v2    = v1 < v2
decodeBOP Gt v1 v2    = v1 > v2
decodeBOP Lte v1 v2   = v1 <= v2
decodeBOP Gte v1 v2   = v1 >= v2
decodeBOP Neq v1 v2   = v1 /= v2

var :: Map I Z
var = Map.fromList [("x", 5),("y", 6),("a", 10)]

test1,test2 :: T
test1 = TApp (Z 5) Plus (TApp (Z 3) Mult (I "x"))

test2 = TApp (Z 10) Plus (I "b")

test3,test4,test5:: B
test3 = BApp test1 Eq (I "y")

test4 = BRead

test5 = BApp TRead Lte (TApp TRead Plus (Z 1))
\end{code}

% -----------------------------------------------------------
%			AUFGABE 4
% -----------------------------------------------------------

\section*{Aufgabe 4}
Schreiben Sie einen Ubersetzer für T und B, für eine einfache Kellermaschine.
\ignore{
\begin{code}

showParse :: [String] -> IO()
showParse xs = do
    putStrLn "------ Parse Outut ----------"
    showParseH xs
        where
            showParseH :: [String] -> IO()
            showParseH []        = putStrLn "------ End of Parse -----"
            showParseH (x:xs)    = do
                putStrLn x
                showParseH xs


\end{code}
}

\begin{code}
parseT :: T -> [String]
parseT (Z v)         		= ["push "++(show v)]
parseT (I v)         		= ["load "++(show v)]
parseT (TApp t1 op t2)		= (parseT t1) ++ (parseT t2) ++ [nameOP op]
parseT TRead                = ["read"]

parseB :: B -> [String]
parseB (Literal b)			= ["push ",(show b)]
parseB (Not b)              = "not":(parseB b)
parseB (BApp t1 bop t2)		= (parseT t1)++(parseT t2)++[nameBOP bop]
paeseB BRead                = ["read"]


nameOP :: OP -> String
nameOP Plus	  = "plus"
nameOP Minus  = "minus"
nameOP Mult   = "mult"
nameOP Div    = "div"
nameOP Mod    = "mod"

nameBOP :: BOP -> String
nameBOP Eq    = "eq"
nameBOP Lt    = "lt"
nameBOP Gt    = "gt"
nameBOP Lte   = "lte"
nameBOP Gte   = "gte"
nameBOP Neq   = "neq"
\end{code}
\label{LastPage}

\end{document}
