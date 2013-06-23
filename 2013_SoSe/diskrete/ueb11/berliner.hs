
ind :: Integer -> Integer
ind 0   =   1
ind 2   =   1
ind 4   =   1
ind _   =   0

sumOf :: Integer -> Integer -> (Integer -> Integer) -> Integer
sumOf a b f   = foldr (+) 0 [f x | x <- [a..b]]

amount = sumOf 0 50 (\x -> sumOf 0 17 (\y -> ind (100 - 2*x - 6*y)))
