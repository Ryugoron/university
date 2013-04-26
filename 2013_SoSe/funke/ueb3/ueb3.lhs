\ignore{

This file contains the solution and the code for the third exercise.
It implements the functions

reverseL,
reverseR,
concatL,
concatR

that can be testet when started with ghci.

}



\section*{Task 1}

Write the function
\begin{lstlisting}
reverse []      =   []
reverse (a:as)  = reverse as ++ [a]
\end{lstlisting}
as foldr and foldl.\\

\textbf{Solution:}\\

The neutral element has to be
\lstinline|e = e' = []| because the empty list always returns the empty list.

In \lstinline|foldr| 
\begin{lstlisting}
f a bs  = {spec}
            bs ++ [a]
        = {shorten}
            flip (++) [a] bs
        =>
      f = flip (.) return (flip (++))
\end{lstlisting}
has to hold. 
Of course we could use the shortened version, but we tried it with a point free version.
This function takes the a as a first argument. This one is bound after the first application of \lstinline|(.)|
by the \lstinline|return| which will pack the `a` in a list. This one will be bound as the second argument in the \lstinline|(++)|.\\

In \lstinline|foldl|
\begin{lstlisting}
    foldl f e (a:as)    
=   {spec foldl}
    foldl f (f e a) as

    f bs a  
=   {spec}
    a : bs
=   {shorten}
    flip (:) bs a
\end{lstlisting}
holds. This spec holds, because \lstinline|bs| is the already reversed first list. Therefor `a` is the element
last to the yet seen list. We have to put it in front.
We get the solution code
\begin{code}
reverseR :: [a] -> [a]
reverseR = foldr (flip (.) return (flip (++))) []

reverseL :: [a] -> [a]
reverseL = foldl (flip (:)) []
\end{code}

\section*{Task 2}

Write the function 
\begin{lstlisting}
concat :: [[a]] -> [a]
concat []       =   []
concat (as:ass) =   as ++ concat ass
\end{lstlisting}
as a \lstinline|foldl| and as a \lstinline|foldr|.\\

\textbf{Solution:}\\

Again by definition
\lstinline|e = e' = []| has to hold.\\

In \lstinline|foldr|
\begin{lstlisting}[mathescape]
f as bs     =   {spec}
                as ++ bs
           $<=>$
f           =   (++)
\end{lstlisting}
has to hold.\\

In \lstinline|foldl|
\begin{lstlisting}
    foldl f e (as:ass)
=   {def. foldl}
    foldl f (f e as) ass
=   {*}
    foldl f (e ++ as) ass
=>
    f = (++)
\end{lstlisting}
holds. Because in (*) \lstinline|bs| is the already concated list up to the list \lstinline|as|
and by the specification \lstinline|as| has to be put at the end.

This leads to the functions
\begin{code}
concatL :: [[a]] -> [a]
concatL = foldl (++) []

concatR :: [[a]] -> [a]
concatR = foldr (++) []
\end{code}

\section*{Task 3}

Proof by induction on listst that
\lstinline|reverse . reverse = id|.\\

\textbf{Solution:}\\
Lemma 1: \lstinline|reverse (as ++ bs) = (reverse bs) ++ (reverse as)|.
Proof: Induction on as.
\begin{lstlisting}
I.A. as = []
    reverse ([] ++ bs)
=   {def. ++)
    reverse bs
=   {def. ++}
    (reverse bs) ++ []
=   {def. reverse}
    (reverse bs) ++ (reverse [])

I.S. as -> (a:as)
    reverse ((a:as) ++ bs)
=   {def. ++}
    reverse (a:(as ++ bs))
=   {def. reverse}
    (reverse (as ++ bs)) ++ [a]
=   {ind. hyp.}
    (reverse bs) ++ (reverse as) ++ [a]
=   {++ assoziative}
    (reverse bs) ++ ((reverse as) ++ [a])
=   {def. ++}
    (reverse bs) ++ (reverse (a:as))
\end{lstlisting}
\mbox{}\hfill$\square$


Proof (main claim):
\begin{lstlisting}
I.A. n = []
    reverse ( reverse [] )
=   {def reverse.1}
    reverse []
=   {def reverse.1}
    []
=   {def. id}
    id []

I.S. n = (a:as)
    reverse ( reverse (a:as) )
=   {def reverse.2}
    reverse ((reverse as) ++ [a])
=   {lemma 1}
    reverse [a] ++ reverse (reverse as)
=   {def. reverse x2}
    [a] ++ reverse (reverse as)
=   {ind. hyp}
    [a] ++ id as
=   {def. id}
    [a] ++ as
=   {def. ++ x2}
    (a:as)
=   {def. id}
    id (a:as)
\end{lstlisting}
\mbox{}\hfill$\square$

\section*{Task 4}

Formulate a Fusion law for \lstinline|foldNat|, \lstinline|foldSTree| and \lstinline|foldPair|.

\begin{description}
    \item[\bfseries foldNat:] This one is defined by
\begin{lstlisting}
foldNat :: (a -> a) -> a -> Nat -> a
foldNat f e O       = e
foldNat f e (S n)   = f (foldNat f e n)
\end{lstlisting}
    We want to find a law of the form
    \lstinline|h . foldNat f e = foldNat f' e'|.\\

    We start with the base.
\begin{lstlisting}
    h (foldNat f e O)
=   {def. foldNat}
    h e
=   {spec, cond A}
    e'
=   {def. foldNat}
    foldNat f' e' O
\end{lstlisting}
    We obtain, that \lstinline|e' = h e| has to hold.\\

\begin{lstlisting}
    h (foldNat f e (S n))
=   {def. foldNat}
    h (f (foldNat f e n))
=   {cond B}
    f' (h (foldNat f e n)))
=   {ind. hyp}
    f' (foldNat f' e' n)
=   {def. foldNat}
    foldNat f' e' (S n)
\end{lstlisting}

    We obtain the condition, that \lstinline|h (f a) = f' (h a)| \\

\begin{theorem}\label{funke:ueb3:foldNatFusion}(FoldNat Fusion Law)\\
    Let \lstinline|f : a -> a| and \lstinline|h : a -> b| be functions and
    \lstinline|e : a| an object.

    Then for any function \lstinline|f' : b -> b| with
    \lstinline|h (f a) = f' (h a)| and \lstinline|e' = h e| the following holds.
    \begin{center}
\begin{lstlisting}
h . foldNat f e = foldNat f' e'
\end{lstlisting}
    \end{center}
\end{theorem}

    \item[\bfseries foldSTree:] This one is defined by
\begin{lstlisting}
foldSTree :: (a -> b -> a -> a) -> a -> Stree b -> a
foldSTree f e Empty             = e
foldSTree f e (Node lt a rt)    = f (foldSTree f e lt) a (foldSTree f e rt)
\end{lstlisting}
    We want to find a law of the form
    \begin{lstlisting}
h . foldSTree f e   = foldSTree f' e'
    \end{lstlisting}
    We begin with the base case.
    \begin{lstlisting}
    h ( foldSTree f e Empty )
=   {def. foldSTree}
    h e
=   {cond A}
    e'
=   {def. foldSTree}
    foldSTree f' e' Empty
    \end{lstlisting}

    We proceede by induction
    \begin{lstlisting}
    h (foldSTree f e (Node lt a rt))
=   {def. foldSTree}
    h (f (foldSTree f e lt) a (foldSTree f e rt))
=   {cond B}
    f' (h (foldSTree f e lt)) a (h (foldSTree f e rt))
=   {ind. hyp}
    f' (foldSTree f' e' lt) a (foldSTree f' e' rt)
=   {def. foldSTree
    foldSTRee f' e' (Node lt a rt)
    \end{lstlisting}
    We obtained a second claim we need, that is
    \lstinline|h (f l a r) = f' (h l) a (h r)|.

\begin{theorem}\label{funke:ueb3:foldStreeFusion}(FoldSTree Fusion Law)\\
    Let \lstinline|f : a -> b -> a -> a| and \lstinline|h : a -> c| be functions
    and \lstinline|e : a| an object.

    Then for \lstinline|f' : c -> b -> c -> c| with
    \lstinline|f' (h l) a (h r) = h (f l a r)| and \lstinline|e' = h e| the following holds.
    \begin{center}
\begin{lstlisting}
h . foldSTree f e   = foldSTree f' e'
\end{lstlisting}
    \end{center}
\end{theorem}
    \item[\bfseries foldPair:] This one is defined by
\begin{lstlisting}
foldPair :: (a -> b -> c) -> (a,b) -> c
foldPair f (a,b)    = f a b
\end{lstlisting}
    We want to find a law of the form
    \begin{lstlisting}
h . foldPair f    = foldPair f'
    \end{lstlisting}
    We begin on the left side and because we have only one case may start with it.
\begin{lstlisting}
    h . foldPair f (a,b)
=   {def. foldPair}
    h ( f a b )
=   {spec}
    f' a b
=   {def. foldPair}
    foldPair f'(a,b)
\end{lstlisting}
    We obtain the following law.
\begin{theorem}\label{funke:ueb3:foldPairfusion}(FoldPair Fusion Law)\\
    Let \lstinline|f : a -> b -> c| be a function and \lstinline|h : c -> d| be a function.
    Then for \lstinline|f' a b = h (f a b)| the following hold
    \begin{center}
        \lstinline|h . foldPair f = foldPair f'|
    \end{center}
\end{theorem}
\end{description}
