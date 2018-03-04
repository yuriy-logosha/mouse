#!/usr/bin/env bash
cd /Users/yura/IdeaProjects/mouse/client/target/classes
RESULT=$(java com/home/mouse/client/Command show)

_RESULT=($RESULT)
_x=${_RESULT[0]}
_y=${_RESULT[1]}
echo x=${_x} y=${_y}
export _x
export _y
