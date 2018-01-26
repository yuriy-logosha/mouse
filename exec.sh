#!/usr/bin/env bash
cd /Users/yura/IdeaProjects/mouse/target/classes
declare RESULT=$(java com/home/Command $1 $2 $3 $4)

echo $RESULT