1.

What is the fold funciton corresponding to the |Either| datatype?
What is its standard prelude equivalence

> data Either a b = Left a | Right b

> foldEither :: (a -> c) -> (b -> c) -> Either a b -> c
> foldEither f _ (Left a)   = f a
> foldEither _ g (Right b)  = g b

The corresponding function is called `either`

2.

Express the function |pow m n = m ^ n| (|m| to the power of |n|)
as a fold

> data Nat = O | S Nat

> foldNat :: (a -> a) -> a -> Nat -> a
> foldNat _ e O      = e
> foldNat f e (S n)  = f $ foldNat f e n

> plus :: Nat -> Nat -> Nat
> plus a b = foldNat S a b

> mult :: Nat -> Nat -> Nat
> mult a b = foldNat (plus a) b

> pow :: Nat -> Nat -> Nat
> pow a b = foldNat (mult a) b

PROOF??

3.

Express the predecessor function |pred :: Nat -> Maybe Nat| as
a fold.

> pred :: Nat -> Maybe Nat
> pred  = foldNat Nil (maybe (Just O) (Just . S))

maybe is the prelude fold on Maybe.

4.

Complete the proof that |`plus`| is commutative

TRALALA.
MOVE TO PDF.

\begin{claim} \label{funke:ueb1:plus:commu}
   The function `plus` is commutative.
\begin{lstlisting}[language=haskell]
plus m n = plus n m
\end{lstlisting}
\end{claim}

To proof this claim we first proof some lemma.

\begin{lemma} \label{funke:ueb1:plus:commu:lemma1}
\begin{lstlisting}[language=haskell]
plus O n = n
\end{lstlisting}
\end{lemma}

\begin{lemma} \label{funke:ueb1:plus:commu:lemma2}
\begin{lstlisting}[language=haskell]
plus m (S n) = plus (S m) n
\end{lstlisting}
\end{lemma}

\textbf{Proof lemma \ref{funke:ueb1:plus:commu:lemma1}.}\\
Induction on $n$.\\
\begin{descritpion}
   \item[\bfseries I.A.] $n=0$\\
\begin{lstlisting}[language=haskell]
plus O O = O
\end{lstlisting}
holds.
   \item[\bfseries I.S.] $n \follows S n$\\
$$\begin{array}{rcl}
\textit{plus 0 (S n)} &\stackrel{\text{Def. plus}}{=}&
      \textit{S (plus 0 n)}\\
   &\stackrel{\text{ind. hyp.}}{=}&
      \textit{S n}
\end{array}$$
By induction the claim holds.\\
\mbox{}\hfill$\square$
\end{description}

\textbf{Proof lemma \ref{funke:ueb1:plus:commu:lemma1}.}\\
Induction on $n$.\\
\begin{description}
   \item[\bfseries I.A.] $n = $O\\
\begin{lstlisting}[language=haskell]
plus m (S O) = S (plus m O)
             = S m
             = plus (S m) 0
\end{lstlisting}
   \item[\bfseries I.S.] $n \follows S n$\\
$$\begin{array}{rcl}
\textit{plus m (S (S n))} &\stackrel{\text{Def. plus}}{=}&
      \textit{S (plus m (S n)}\\
   &\stackrel{\text{ind. hyp.}}{=}&
      \textit{S (plus (S m) n)}\\
   &\stackrel{\text{Def. plus}}{=}&
      \textit{plus (S m) (S n)}
\end{array}$$
\end{description}
By induction the claim holds.\\
\mbox{}\hfill$\square$

\textbf{Proof claim \ref{funke:ueb1:plus:commu:lemma1}.}\\
Let $m$ be arbitrary but fixed.\\
Induction on $n$.\\
\begin{description}
   \item[\bfseries I.A.] $n=$O\\
      $$\begin{array}{rcl}
         \textit{plus m O} &\stackrel{\text{Def. plus}}{=}& \textit{m} \\
         &\stackrel{\text{lemma 1}}{=} \textit{plus O m}
      \end{array}$$
   \item[\bfseries I.S.] $n \follows S n$\\
      $$\begin{array}{rcl}
         \textit{plus m (S n)} &\stackrel{\text{Def. plus}}{=}&
            \textit{S (plus m n)}\\
         &\stackrel{\text{ind. hyp.}}{=}&
            \textit{S (plus n m)}\\
         &\stackrel{\text{Def. plus}}{=}&
            \textit{plus n (S m)}\\
         &\stackrel{\text{lemma 2}}{=}&
            \textit{plus (S n) m}
      \end{array}$$
\end{description}
By induction the claim holds.\\
\mbox{}\hfill$\square$
