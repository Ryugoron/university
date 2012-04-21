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

\begin{code}
module WhileParse where

import Data.Map (Map)
import qualified Data.Map as Map
\end{code}

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
data BOP = EQ | LT | GT | LTE | GTE | NEQ
\end{code}

Induktiv aufgebaute Einheiten:
\begin{code}
data T = Z Z
       | I I
       | TApp T OP T
       | TRead

data B = Literal B
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

type P = C
\end{code}


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
                (BApp (I "x") GTE (I "y"))
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
		-> (Z, [K])              -- Tupel (Rückgabewert (Zahl), Input)
evalT (Z v) _ e       = (v,e)
evalT (I v) m e       =
	let mvalue = Map.lookup v m in
		case mvalue of
			Just value	->  (v, e)
			Nothing 	-> error "No Variable of the name "+v+" was found"
evalT (TApp t1 op t2) m e =
	let (v1, e1) = evalT t1 m e in
		let (v2, e2) = evalT t2 m e1 in
			(decodeOP op v1 v1, e2)
evalT (TRead) _ (e:es) = (e,es)
evalT _ _ _			   = error "Not enough Input"

		
evalB :: B                       -- boolscher Term
         -> Map T Z              -- Variablenbelegung
		 -> [K]                  -- List von Eingaben
		 -> (W, [K])             -- Rückgabewert (Bool)
evalB (Literal b) _ e       = (b,e)
evalB (Not b) _ e           = (not b, e)
evalB (BApp t1 bop t2) m e  =
	let (b1, e1) = evalT t1 m e in
		let (b2, e2) = evalT t2 m e1 in
			(decodeBOP bop b1 b2, e2)
evalB BRead m (e:es)        = (e,es)
evalB _ _ _                 = error "Not enough Input."
		 
		 
decodeOP :: OP -> Z -> Z -> Z
decodeOP Plus v1 v2     = v1 + v2
decodeOP Minus v1 v2    = v1 - v2
decodeOP Mult v1 v2     = v1 * v2
decodeOP Div v1 v2      = v1 / v2
decodeOP Mod v1 v2      = v1 `mod` v2

decodeBOP :: BOP -> Z -> Z -> W
decodeBOP EQ v1 v2    = v1 == v2
decodeBOP LT v1 v2    = v1 < v2
decodeBOP GT v1 v2    = v1 > v2
decodeBOP LTE v1 v2   = v1 <= v2
decodeBOP GTE v1 v2   = v1 >= v2
decodeBOP NEQ v1 v2   = v1 /= v2
\end{code}

% -----------------------------------------------------------
%			AUFGABE 4
% -----------------------------------------------------------

\section*{Aufgabe 4}
Schreiben Sie einen Ubersetzer für T und B, für eine einfache Kellermaschine.

\begin{code}

parseT :: T -> [String]
parseT (Z v)         		= ["push "+v]
parseT (I v)         		= ["load "+v]
parseT (Tapp t1 op t2)		= (nameOP op)++ (parseT t1) ++ (parseT t2)
parseT TRead                = ["read"]

parseB :: B -> [String]
parseB (Literal b)			= ["push "+b]
parseB (Not b)              = ["not","push "+b]
parseB (BApp t1 bop t2)		= (nameBOP bop)++(parseT t1)++(parseT t2)
paeseB BRead                = ["read"]

nameOP :: OP -> String
nameOP Plus	  = "plus"
nameOP Minus  = "minus"
nameOP Mult   = "mult"
nameOP Div    = "div"
nameOP Mod    = "mod"

nameBOP :: BOP -> String
nameBOP EQ    = "eq"
nameBOP LT    = "lt"
nameBOP GT    = "gt"
nameBOP LTE   = "lte"
nameBOP GTE   = "gte"
nameBOP NEQ   = "new"
\end{code}

\label{LastPage}

\end{document}
