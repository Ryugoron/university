>module Main  where

>import Data.Maybe
>import qualified Data.Map as M
>import qualified Data.Set as S
>import System.Environment

Getting the data file into a typed list

>load :: String -> IO [(String, String, Integer)]
>load filepath = do
>  file <- readFile filepath
>  return $ catMaybes $ map handleLine (lines file)

>handleLine :: String -> Maybe (String, String, Integer)
>handleLine input  = case words input of
>  [from,to,dist]  -> Just (from, to, read dist)
>  _               -> Nothing

Building a Graph from the data.

>inputToGraph :: [(String, String, Integer)] -> Graph String Integer
>inputToGraph = foldl (\g (f,t,ft) -> (insertG t f ft g)) (G (S.empty) (M.empty))

List of vertices and a adjacence list.

Definition of a Graph (adjacence list)

>data Graph v e = G (S.Set v) (M.Map v [(v,e)]) deriving Show

>insertG :: (Ord v, Ord e) => v -> v -> e -> Graph v e -> Graph v e
>insertG f t e (G nodes edges)   = 
>  let
>      fList = M.lookup f edges
>  in
>      G (S.insert f $ S.insert t nodes) (M.insert f (maybe [(t,e)] ((:) (t,e)) fList) edges)

Definition fo a Tree (weighted edges)

>data Tree v e = Node v [(Tree v e, e)] deriving Show

>member :: (Eq v) => v -> Tree v e -> Bool
>member a (Node v sub)
>   | a == v    = True
>   | otherwise = foldr (||) False $ map ((member a).fst) sub

>insertT :: (Eq v) => v -> v -> e -> Tree v e -> Tree v e
>insertT a b ab (Node v sub)  
>   | a == v    = Node v ((Node b [],ab):sub)
>   | otherwise = Node v $ map (\(t,e) -> (insertT a b ab t, e)) sub

Definition of prim

>prim :: (Ord v, Ord e) => v -> Graph v e -> Tree v e
>prim s g@(G v e) = primIt g (Node s []) $ foldr insSort [] [(s,a,b) | (a,b) <- e M.! s]

>primIt :: (Ord v, Ord e) => Graph v e -> Tree v e -> [(v,v,e)] -> Tree v e
>primIt _ t []              = t
>primIt (G v e) t ((a,b,ab):xs)
>   | member b t    = primIt (G v e) t xs
>   | otherwise     = primIt (G v e) (insertT a b ab t) $ foldr insSort xs [(b,x,y) | (x,y) <- e M.! b] 

Insertion sort for the next vertices.

>insSort :: (Ord a) => a -> [a] -> [a]
>insSort x []      = [x]
>insSort x (y:ys)
>  | x <= y        = x : y :ys
>  | otherwise     = y : insSort x ys

>treeToDot :: (Show v, Show e) => Tree v e -> String
>treeToDot t@(Node v _) = "graph g {\nnode [shape=plaintext]\n"++(show v)++";"++itTree t++ "\n}"
>   where
>       itTree (Node v [])      = ""
>       itTree (Node v sub)     = concat $ (map (\((Node a _),e) -> "\n"++(show v)++"--"++(show a)++" [label="++(show e)++"];") sub) ++ (map (\(t,_) -> itTree t) sub)

>main :: IO ()
>main = do
>   args <- getArgs
>   handleArgs args
>   return ()
>       where
>           handleArgs [dat,start,target]    = do
>               putStrLn dat
>               putStrLn start
>               putStrLn target
>               f <- load dat
>               writeFile target $ treeToDot $ prim start $ inputToGraph f
>               return ()
>           handleArgs _        = error $ "Usage : prim [source] [start node] [target file]"
