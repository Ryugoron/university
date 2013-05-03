#!/bin/sh

if [ $# \< 3 ] 
then
    echo "Useage : prim [source.dat] [start node] [target]"
    exit 1
else
    echo "Computing MST in $1..."
    ./prim $1 $2 "$3.dot"
    echo "Converting tree to png ..."
    dot "$3.dot" -Tpng > "$3.png"
    echo "Cleanup..."
    rm "$3.dot"
    echo "Done!"
    exit 0
fi
