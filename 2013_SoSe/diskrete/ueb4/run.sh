#!/bin/sh

if [ $# \< 3 ] 
then
    echo "Useage : prim [source.dat] [start node] [target]"
    exit 1
else
    ./prim $1 $2 "$3.dot"
    dot "$3.dot" -Tpng > "$3.png"
    rm "$3.dot"
    exit 0
fi
