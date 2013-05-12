Solutions to excercise 5.
1.

>   data List a  =  Wrap a | Snoc (List a) a

-###############################################
Fold
-##############################################

The fold function for (List a), given the following ingredients:
  List a      :: *
  Wrap        :: a -> List a
  Snoc        :: List a -> a -> List a
is given by

>   foldList :: (x -> a -> x) -> (a -> x) -> List a -> x
>   foldList f g (Wrap a) = g a
>   foldList f g (Snoc as a) = f (foldList f g as) a

-#################################################
Unfold
-################################################

For the corresponding unfold function, we first identify the destruction function

>   uncoList            :: List a -> Either a (List a, a)
>   uncoList (Wrap a)   = Left a
>   uncoList (Snoc as a)= Right (as, a)

Then, the unfold is given by

>   unfoldList          :: (x -> Either a (x, a)) -> x -> List a
>   unfoldList f x      = case f x of
>                            Left a        -> Wrap a
>                            Right (as, a) -> Snoc (unfoldList f as) a 

-####################################################
Fold fusion: h . foldList f g = foldList f' g'
-####################################################
Determine the identities by applying specifications and definitions

(1)
  h (foldList f g (Wrap a))
= {def. foldList}
  h (g a)
= folgList f' g' (Wrap a)
<=> g' a = h (g a)
<=> g' = h.g

(2)
  h (foldList f g (Snoc as a))
= {def. foldList}
  h (f (foldList f g as) a)
= {should be}
  f' (h (foldList f g as)) a
= {spec.}
  f' (foldList f' g' as) a
= {def.}
  foldList f' g' (Snoc as a)
<= h  (f x a) = f' (h x) a
  where x = foldList f g as

Then, it holds that h . foldList f g = foldList f' g'
if  g' = h.g   and   f' (h x) y = h (f x y)

-##################################################
Fold-Unfold-Fusion law: foldList f g . unfoldList h = z
-##################################################
Again, applying specification and definitions

    z x
= {def. z}
  foldList f g (unfoldList h x)
= {def. unfoldList}
  foldList f g (case h x of
                  Left a        -> Wrap a
                  Right (as, a) -> Snoc (unfoldList h as) a)

(1) Case h x of Left a

  foldList f g (case h x of
                  Left a        -> Wrap a
                  Right (as, a) -> Snoc (unfoldList h as) a)
= {by assumption}
  foldList f g (Wrap a)
= {def. foldList}
  g a

(2) Case h x of Right (as, a)
  foldList f g (case h x of
                  Left a        -> Wrap a
                  Right (as, a) -> Snoc (unfoldList h as) a)
= {by assumption}
  foldList f g (Snoc (unfoldList h as) a))
= {def. foldList}
  f (foldList f g (unfoldList h as)) a
= {def. z}
  f (z as) a

Hence, it follows that

    foldList f g . unfoldList h = z
<=>
    z x = case h x of
            Left a        -> g a
            Right (as, a) -> f (z as) a

2.  The standard function |map :: (a -> b) -> [a] -> [b]| is 
    defined by

<   map f []        =  []
<   map f (a : as)  =  f a : map f as

    Prove that |map id = id| and |map (g . f) = map g . map f|.

Proof by induction:
(1) map id = id
Base step: map id [] = [] = id []
Induction step:
map id (a:as) 
= {def.} id a: map id as
= {def id} a: map id as
= {ind. hyp} a: id as
= {def id} a : as
= {def id} id (a:as)                  []

(2) map (g.f) = map g . map f
Base step: map g (map f []) = map g [] = [] = map (g.f) []
Induction step:
map g (map f (a:as))
= {def. map} map g (f a: map f as)
= {def. map} g (f a): map g (map f as)
= {ind. hyp} g (f a): map (g.f) as
= (g.f) a : map (g.f) as
= map (g.f) (a:as)                      []


3.  Define a function |mapSTree :: (a -> b) -> STree a -> STree b| such
    that |mapSTree id = id| and |mapSTree (g . f) = mapSTree g . mapSTree f|.
    Prove that that is indeed the case.

>   data STree a                  =  Empty | Node (STree a) a (STree a)

>   mapSTree :: (a -> b) -> STree a -> STree b
>   mapSTree f Empty = Empty
>   mapSTree f (Node left a right) = Node (mapSTree f left) (f a) (mapSTree f right)

Proposition: mapSTree id = id
Proof by induction:
Base step: mapSTree id Empty = Empty = id Empty
Induction step:
mapSTree id (Node left a right)
= {def.} Node (mapSTree id left) (id a) (mapSTree id right)
= {2x ind. hyp} Node (id left) (id a) (id right)
= Node left a right
= id (Node left a right)                    []

Proposition: mapSTree (g.f) = mapSTree g . mapSTree f
Proof by induction:
Base step: mapSTree g (mapSTree f Empty) = mapSTree g Empty = Empty = mapSTree (g.f) Empty
Induction step:
mapSTree g (mapSTree f (Node left a right))
= mapSTree g (Node (mapSTree f left) (f a) (mapSTree f right))
= Node (mapSTree g (mapSTree f left)) (g (f a)) (mapSTree g (mapSTree f right))
= Node (mapSTree (g.f) left) (g (f a)) (mapSTree (g.f) right)
= mapSTree (g.f) (Node left a right)                    []

4.  Show that for all |f|, |concat . map (map f) = map f . concat|.

lemma 1: (map f a) ++ (map f b) = map f (a ++ b)
proof by induction over a
base: map f ([] ++ b) = map f b = [] ++ map f b = map f [] ++ map f b
induction step:
  map f ((a:as) ++ b)
= map f (a: (as ++ b))
= f a : (map f (as ++ b))
= {ind. hyp} f a : ((map f as) ++ (map f b))
= (f a: map f as) ++ (map f b)
= map f (a:as) ++ (map f b)                    []

lemma 2: (map f a) ++ (map f (concat as)) = map f (a ++ concat as) for all lists as
proof by induction
base: (map f a) ++ (map f (concat [])) 
= (map f a) ++ (map f [])
= (map f a) ++ []
= map f a 
= map f (a ++ [])
= map f (a ++ concat [])
step: 
(map f a) ++ (map f (concat b:as))
= (map f a) ++ (map f (b ++ concat as))
= {ind hyp.} (map f a) ++ ((map f b) ++ (map f (concat as)))
= (associativity ++) ((map f a) ++ (map f b)) ++ (map f (concat as))
= {lemma 1} (map f (a ++ b)) ++ (map f (concat as)) 
= {ind hyp.} map f ((a ++ b) ++ concat as)
= map f (a ++ b ++ concat as)
  map f (a ++ concat (b:as))                    []

Now the actual proposition
<   |concat . map (map f) = map f . concat|

Proof by induction
Base step: concat (map (map f) []) = concat [] = [] = map f [] = map f (concat [])
Induction step:
concat (map (map f) (a:as))
= concat (map f a: map (map f) as)
= (map f a) ++ concat (map (map f) as)
= {ind. hyp} (map f a) ++ (map f (concat as))
= {lemma 2} map f (a ++ concat as)
= map f (concat a:as)                    []

5.  Show that, for all |f|, |flatten . mapSTree f = map f . flatten|
    (we have defined |flatten| in Lecture 3 and also used it in 
    Lecture 4).

< flatten                       =  foldSTree glue []
<                                  where
<                                  glue las a ras  =  las ++ [a] ++ ras

Proposition: flatten . mapSTree f = map f . flatten
Proof by induction:
Base case:
  flatten (mapSTree f Empty)
= flatten Empty = [] = map f [] = map f (flatten Empty)
Induction step:
  flatten (mapSTree f (Node left a right))
= flatten (Node (mapSTree f left) (f a) (mapSTree f right))
= flatten (mapSTree f left) ++ [f a] ++ flatten (mapSTree f right)
= {ind. hyp.}map f (flatten left) ++ [f a] ++ map f (flatten right)
= map f (flatten left) ++ (f a:(map f (flatten right))
= map f (flatten left) ++ map f (a:flatten right)
= {lemma 1 from 4.} map f (flatten left ++ a:flatten right)
= map f (flatten left ++ [a] ++ flatten right)
= map f (flatten (Node left a right))                    []
