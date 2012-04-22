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
\end{code}

Die Formulierung von algebraischen Datentypen findet in Haskell schon in einer grammatikartgen Form statt. Für die einzelnen Terme müssen nur noch eindeutige Konstruktoren vergeben werden, sonst kann alles genau so übernommen werden.

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
    show (Id i)              = i
    show (TApp t1 op t2)    = '(':(show t1)++(nameOp op)++(show t2)++")"
    show (TRead)            = "'read'"

instance Show B where
    show (Literal b)        = show b
    show (Not b)            = "not ("++(show b)++")"
    show (BApp t1 bop t2)   = '(':(show t1) ++ (nameBop bop) ++ (show t2)++")"
    show BRead              = "'read'"

showC :: C -> [String]
showC = showCH 0    where
    showCH :: Int -> C -> [String]
    showCH n Skip                  = [(nSpace n)++"skip"]           
    showCH n (Assign i t)          = [(nSpace n)++(show i)++" := "++(show t)]
    showCH n (Seq c1 c2)           = (showCH n c1)++(showCH n c2)
    showCH n (If b c1 c2)          = [(nSpace n)++"IF "++(show b)++" THEN"]++(showCH (n+4) c1)++[(nSpace n)++"ELSE"]++(showCH (n+4) c2)++[(nSpace n)++"FI"]
    showCH n (While b c)           = [(nSpace n)++"WHILE "++(show b)++" DO"]++(showCH (n+4) c)++[(nSpace n)++"OD"]
    showCH n (BOut b)              = [(nSpace n)++"output "++(show b)]
    showCH n (TOut t)              = [(nSpace n)++"output "++(show t)]

nSpace :: Int -> String
nSpace n = (take n).repeat $ ' '

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
                (BApp (Id "x") Gte (Id "y"))
                   (Seq
                    (Assign ("g") (TApp (Id "g") Plus (Z 1)))
                    (Assign ("x") (TApp (Id "x") Minus (Id "y")))))
            (TOut (Id "g"))
\end{code}

\ignore{
\begin{code}
divTest :: P
divTest = Seq
            (Assign "x" TRead)
            (Seq
            (Assign "y" TRead)
            (Seq
            (Assign "g" (Z 0))
            divprog))
\end{code}
}

Die Konstante sollte sich von selbst erklären. Zunächst brauchen wir eine Sequenz, der erste Teil ist die Berechnung, der zweite die Asugabe. Im While machen wir den vergleich, ob x größer als y ist. Innerhalb der Schleife incrementieren wir g einmal und ziehen von x y ab.

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
evalT (Id v) m e       =
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
\end{code}

Bei beiden geben wir im einfachen (Z, Literate) Fall den Wert infach zurück. Beim Identifier suchen wir in der Variablenbelegung nach dem Wert und geben diesen, sofern vorhanden, zurück. Bei der Anwandung der Operatoren werten wir ersten linken Term aus, geben die neue Eingabeliste in die rechte Berechnung und setzten danach den Wert zusammen.

\begin{code}
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

\end{code}		 

\ignore{
\begin{code}

evalC :: C -> [K] -> (Map I Z, [K], [K])
evalC c e = evalCH c (Map.fromList []) e []

evalCH :: C                      -- Programm
        -> Map I Z              -- Variablen
        -> [K]                  -- Eingabe
        -> [K]                  -- Ausgabe
        -> (Map I Z, [K], [K])  -- (Variablen, Eingabe, Ausgabe)
evalCH Skip m e a            = (m, e, a)
evalCH (Assign i t) m e a    = 
    let (v,e1) = evalT t m e in
        ((Map.insert i v m), e1, a)
evalCH (If b c1 c2) m e a    =
    let (bed, e1) = evalB b m e in
        case bed of
            True    -> let (m1,ein1,aus1) = evalCH c1 m e1 a in
                (m1, ein1, aus1)
            False   -> let (m2,ein2,aus2) = evalCH c1 m e1 a in
                (m2, ein2, aus2)
evalCH (While b c) m e a     =
    let (bed, e1) = evalB b m e in
        case bed of
            True    -> evalCH (Seq c (While b c)) m e1 a
            False   -> (m,e,a)
evalCH (Seq c1 c2) m e a     =
    let (m1,ein1,aus1) = evalCH c1 m e a in
        evalCH c2 m1 ein1 aus1
evalCH (BOut b) m e a            = 
    let (bed, e1) = evalB b m e in
        (m,e1,a++[LiteralBool bed])
evalCH (TOut t) m e a            =
    let (erg, e1) = evalT t m e in
        (m,e1,a++[LiteralInteger erg])
\end{code}
}

Die folgende Operation sorgt dafür, dass die boolschen und arithmetischen Operationen korrekt umgesetzt werden. Falls es von intresse ist, was darin steht, wird empfohlen in das Programm zu sehen. Wir habe darüber hinaus fünf Testfälle (test1, ... , test5) und die Konstante (var), die schon 3 Variablen vorbelegt enthält damit man das ganze einmal testen kann.

\begin{code}		 
decodeOP :: OP -> Z -> Z -> Z
decodeBOP :: BOP -> Z -> Z -> W
\end{code}

\ignore{
\begin{code}
decodeOP Plus v1 v2     = v1 + v2
decodeOP Minus v1 v2    = v1 - v2
decodeOP Mult v1 v2     = v1 * v2
decodeOP Div v1 v2      = v1 `div` v2
decodeOP Mod v1 v2      = v1 `mod` v2
\end{code}
}

\ignore{
\begin{code}
decodeBOP Eq v1 v2    = v1 == v2
decodeBOP Lt v1 v2    = v1 < v2
decodeBOP Gt v1 v2    = v1 > v2
decodeBOP Lte v1 v2   = v1 <= v2
decodeBOP Gte v1 v2   = v1 >= v2
decodeBOP Neq v1 v2   = v1 /= v2
\end{code}
}


\ignore{
\begin{code}

var :: Map I Z
var = Map.fromList [("x", 5),("y", 6),("a", 10)]

test1,test2 :: T
test1 = TApp (Z 5) Plus (TApp (Z 3) Mult (Id "x"))

test2 = TApp (Z 10) Plus (Id "b")

test3,test4,test5:: B
test3 = BApp test1 Eq (Id "y")

test4 = BRead

test5 = BApp TRead Lte (TApp TRead Plus (Z 1))
\end{code}
}
% -----------------------------------------------------------
%			AUFGABE 4
% -----------------------------------------------------------

\section*{Aufgabe 4}
Schreiben Sie einen Ubersetzer für T und B, für eine einfache Kellermaschine.
\ignore{
\begin{code}

showNice :: [String] -> IO()
showNice xs = do
    putStrLn "------ Programm ----------"
    showNiceH xs
        where
            showNiceH :: [String] -> IO()
            showNiceH []        = putStrLn "-------- End --------"
            showNiceH (x:xs)    = do
                putStrLn x
                showNiceH xs

showErg :: (Map I Z, [K], [K]) -> IO ()
showErg (m,e,a) = do
    putStrLn "Variablen:"
    showVar (getList m)
    putStrLn "Ausgabe:"
    showOut a 
        where
            getList = Map.foldrWithKey (\k v l -> (show k,show v):l) []
            showVar [] = putStrLn ""
            showVar ((k,v):xs)  = do
                putStrLn (k++" := "++v)
                showVar xs
            showOut [] = putStrLn ""
            showOut (x:xs) = do
                putStrLn (show x)
                showOut xs

\end{code}
}

\textbf{Lösung:}\\

Das parsen passiert hier wieder Rekursiv durch den Baum, indem wir immer zuerst links und danach rechts in die Unterbäume absteigen.

\begin{code}
parseT :: T -> [String]
parseT (Z v)         		= ["PUSH "++(show v)]
parseT (Id v)         		= ["LOAD "++(show v)]
parseT (TApp t1 op t2)		= (parseT t1) ++ (parseT t2) ++ [nameOP op]
parseT TRead                = ["READ"]

parseB :: B -> [String]
parseB (Literal b)			= ["PUSH ",(show b)]
parseB (Not b)              = "NOT":(parseB b)
parseB (BApp t1 bop t2)		= (parseT t1)++(parseT t2)++[nameBOP bop]
paeseB BRead                = ["READ"]
\end{code}

\ignore{
\begin{code}
parseC :: C -> [String]
parseC = fst.parseCH 0
    where
        parseCH n (Skip)           = (["HALT 0,0,0"],n)
        parseCH n (Assign i t)     = ((parseT t)++["STORE "++(show i)],n)
        parseCH n (If b c1 c2)     =
            let (code1, counter1) = parseCH (n+1) c1 in
                let (code2, counter2) = parseCH counter2 c2 in
                    ((parseB b)++["CB iflabel"++(show n)]++code2++["JMP endiflabel"++(show n),"iflabel"++(show n)++":"]++code2++["endiflabel"++(show n)],counter2)
        parseCH n (Seq c1 c2)   =
            let (code1, counter1) = parseCH n c1 in
                let (code2, counter2) = parseCH counter1 c2 in
                    (code1++code2,counter2)
        parseCH n (While b c)     =
            let (code, counter) = parseCH (n+1) c in
                (["whilelabel"++(show n)++":"]++(parseB b)++["CB endwhilelabel"++(show n)]++code++["JMP whilelabel"++(show n),"endwhilelabel"++(show n)],counter)
        parseCH n (TOut t)       = ((parseT t)++["PRINT"],n)
        parseCH n (BOut b)       = ((parseB b)++["PRINT"],n)
\end{code}
}

Diese Operation übersetzt die Operationen des Konstruktorterms in die Befehle des Stackautomaten. Hier findet nur ein Namensmapping statt, kann aber wahlwiese wiederum im Dokument nachgelesen werden.

Testen kann man das ganze an den selben Fälle, wie Aufgabe 3. Wenn man das ganze mit 'showNice.parseT \$ test1' startet, wird die Ausgabe auch noch schön formatiert.
\begin{code}
nameOP :: OP -> String
nameBOP :: BOP -> String
\end{code}

\ignore{
\begin{code}
nameOP Plus	  = "PLUS"
nameOP Minus  = "MINUS"
nameOP Mult   = "MULT"
nameOP Div    = "DIV"
nameOP Mod    = "MOD"

nameBOP Eq    = "EQ"
nameBOP Lt    = "LT"
nameBOP Gt    = "GT"
nameBOP Lte   = "LTE"
nameBOP Gte   = "GTE"
nameBOP Neq   = "NEQ"
\end{code}}
\label{LastPage}

\end{document}
