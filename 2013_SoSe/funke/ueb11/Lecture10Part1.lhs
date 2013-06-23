> module Lecture10Part1 where

> pair (f, g) a = (f a, g a)

Propositional logic
-------------------

Propositional logic is usually presented in natural deduction form,
with introduction, and elimination rules for the operators.  For 
example, here are the rules for conjunction:


Introduction rule:

                          p1     p2              
                        -------------  andI
                          p1 And p2               

Elimination rule:

          p1 And p2                     p1 And p2               
        -------------  andL          -------------  andER
              p1                            p2

It is important to understand that |p1| and |p2| are \emph{not}
propositions!  A simplified interpretation is that they are
``propositional variables'', and a rule such as |andI| is a template,
which, once we have substituted propositions for the variables, allows
us to infer the conclusion from the premises.

Now consider the following typical example:


         p1 And p2                     p1 And p2
        ----------- andER             ----------- andEL
             p2                            p1
      --------------------------------------------- andI
                         p2 And p1

This kind of derivation tree is intuitive and easy to understand, but
logic is not an intuition-friendly subject.  There are many problems
here.  The most obvious one is that, whatever these things above the
long dashed line marked |andI| might be, they are neither
propositions, nor propositional variables.  And despite the fact we
just cut-and-pasted them from above, they are not rules either.
Rather, they appear to be derivations.  That, in turn, implies that
the variables |p1| and |p2| are not filled in with propositions, but
with \emph{evidence} for propositions.

This means that |andI| and so on behave somewhat like functions.  
Instead of taking elements of sets to elements of a target set, 
they take evidence for propositions (i.e. for the premises) to evidence
for the conclusion.

With this understanding, we can introduce the following notation,
which is more formal (while still being quite suggestive).

> data And p1 p2          -- a ``formation'' rule

Introduction rule

> andI                ::  (p1, p2) -> And p1 p2

Elimination rules

> andEL               ::  And p1 p2 -> p1
> andER               ::  And p1 p2 -> p2

Example:

> ex1                 ::  And p1 p2 -> And p2 p1
> ex1                  =  andI . pair (andER, andEL)

Notice that we are only interested in the type checking: the implementations
are irrelevant (all declared functions representing inference rules
are implemented at the end of the file with |undefined|, only for the
purpose of type checking).  If we had made a mistake, such as

< ex1                  =  andI . pair (andER, andER)

the type checker would have complained.

Let us introduce disjunction.

Introduction rules:

             p1                            p2
        -------------  orIL          -------------  orIR
          p1 Or p2                      p1 Or p2               

These are ``dual'' to the conjunction rules, but the elimination
rule for conjunction is much more interesting:

                            p1       p2
                            .        .
                            .        .
                            .        .
           p1  Or  p2       p        p
          -----------------------------------  orE
                             p

Conjunction elimination is also known as ``proof by cases''.

Translating this into our formal notation, we obtain:

> data Or p1 p2

Introduction rules:

> orIL                ::  p1 -> Or p1 p2
> orIR                ::  p2 -> Or p1 p2

Elimination rule:

> orE                 ::  (Or p1 p2, p1 -> p, p2 -> p) -> p

Examples:

> ex2                 ::  Or p1 p2 -> Or p2 p1
> ex2 o                =  orE (o, orIR, orIL)

Negation
--------

There are many ways of introducting negation.  The one adopted here 
follows ``The Language of First-Order Logic'' by Barwise and Etchemendy,
sadly out of print, but you can buy it used from amazon.com for 0.01$ 
and from amazon.de for 3,99 EUR.  There exists a ``successor'' book,
called ``Language, Proof, and Logic'', which I don't like quite as much.

Introduction:

                         p
                         .
                         .
                         .
                    q And (Neg q)
                  ----------------- negI
                       Neg p

This is ``proof by contradiction'', or ``reduction to the absurd''.
Notice the somewhat usettling feature that we already need to have
a negation in order to produce a negation.

Elimination:
                     Neg (Neg p)
                  ----------------- negE
                          p

This is also known as ``the law of double negation''.

In our notation, negation is presented as follows.

> data Neg p

> negI                ::  (p -> And q (Neg q)) -> Neg p
> negE                ::  Neg (Neg p) -> p

Examples:

> ex3                 ::  (p -> Neg p) -> Neg p
> ex3 f                =  negI (andI . pair (id, f))

> ex4                 ::  Neg (Or p (Neg p)) -> p
> ex4 no               =  negE (negI (\ np -> andI (orIR np, no)))

> ex5                 ::  Neg (Or p (Neg p)) -> Neg p
> ex5 no               =  undefined

> ex6                 ::  Or p (Neg p)    -- the law of excluded middle
> ex6                  =  undefined

In all these cases, the type checker will protest if we make a
mistake.  As before, the implementation of the given inference
rules, on which we build our examples, is irrelevant.

Moral:
                +----------------------------------+
                |                                  |
                  Proof Checking  =  Type Checking |
                |                                  |
                +----------------------------------+

Can we extend this beyond the propositional calculus?

--------------------------------------------

> andI                 =  undefined
> andEL                =  undefined
> andER                =  undefined
> orIL                 =  undefined
> orIR                 =  undefined
> orE                  =  undefined
> negI                 =  undefined
> negE                 =  undefined

