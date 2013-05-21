Exercises 6
-----------

1.  Characterize the polymorphic functions of type of type 
    |(a, a) -> (a, a)|, using the fact that |F a = (a, a)|
    (the ``diagonal'' functor) is covariant (``normal'').

Here, |F a = G a = (a,a)| are covariantfunctors where
the |fmap| functions satisfies the type |F a -> F b|, so
|(a,a) -> (b,b)|, for a function |f :: a -> b|.
Hence, |fmap f (a,b) = (f a, f b) = : |.

Let |alpha| be a polymorphic function |alpha :: F a -> G a|.

By parametricity, for all functions f it holds that

        alpha . fmap f = fmap f . alpha

Let |a = ()|. Then, |alpha ((),()) = ((),())|.
     Next, consider any type |X| and an arbitrary element |x :: X|.
      Let |f :: () -> X|, |f () = x|. Then
  
      alpha (fmap f ((),())) = fmap f (alpha ((),()))
  <=> alpha (f (), f ()) = fmap f ((), ())
  <=> alpha (x, x) = fmap f ((),())
  <=> alpha (x, x) = (f (), f () ) = (x, x)


2.  Characterize the polymorphic predicate transformers
    |beta :: (a -> Bool) -> (a -> Bool)| using the fact that
    |F a = a -> Bool| is a contravariant functor.

    Hint:  Try to characterize |beta| in terms of its
    action on |() -> Bool|.

3.  Complete the calculation for the |filter| example in the
    lecture summary.

4.  Derive the naturality conditions for polymorphic functions
    |alpha :: F1 a -> G1 a| and |beta :: F2 a -> G2 a|, where
    |F1, G1| are covariant functors and |F2, G2| are contravariant
    functors, from the dinaturality condition, i.e., show that
    for any types |A|, |B|, and any function | f :: A -> B|, we
    have

         alpha . fmap f =  fmap f . alpha

    and
    
         beta . cofmap f   =  cofmap f . beta
    
    using the dinaturality condition.  You'll have to first
    define polymorphic functions |alpha'| and |beta'| to which
    the dinaturality condition can be applied, similarly to
    the way we proceeded with |filter|.


