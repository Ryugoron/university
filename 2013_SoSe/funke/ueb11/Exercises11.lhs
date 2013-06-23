Exercises 11
------------

The following exercises are meant to familiarize you with formal 
proofs in our abstract Haskell and Idris notation.

Exercises 1-3 refer to Lecture10Part1.lhs

1.  Complete the implementation of |ex2|.  

2.  Prove that |Or p1 (And p2 p3)| entails |And (Or p1 p2) (Or p1 p3)|
    (this is a distributive law, perhaps easier to see in the infix
    notation: |p1 Or (p2 & p3)| implies |(p1 Or p2) & (p1 Or p3)|.

3.  Complete the implementation of |ex5| and that of |ex6| (which uses 
    both |ex4| and |ex5|).

Exercises 4-5 refers to Lecture10Part2.lidr.

4.  Write down the type of |ex8| in English: ``there exists ... such that for 
    all ... implies that for all ... there exists ...''. (This is meant
    to check that you understand the Idris notation for quantifiers.)

5.  Complete the implementation of |ex8|, by implementing |f|.

The next exercises should improve your appreciation of the Curry-Howard
isomorphism.  They refer to the file Lecture10Part3.lidr.

6.  Translate the type of the distributive law from exercise 2 above
    using the concrete representations of |Or| and |And|, and implement 
    it using  pattern-matching instead of introduction and elimination laws.

7.  Translate |ex5| using the concrete representations for |Neg| and |Or|
    and implement it using pattern-matching instead of introduction and
    elimination rules.

8.  Implement |ex8| using pattern-matching.  Make sure you check that
    the typing of |ex8| still says them same thing you figured out in
    exercises 4 above.

