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
Eingabe als Parameter erhält.

% -----------------------------------------------------------
%			AUFGABE 4
% -----------------------------------------------------------

\section*{Aufgabe 4}
Schreiben Sie einen Ubersetzer für T und B, für eine einfache Kellermaschine.


\label{LastPage}

\end{document}
