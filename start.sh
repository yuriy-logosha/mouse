#!/usr/bin/env bash

#kill -9 `cat mouse-server.pid`
rm mouse-server.pid
java -Dport=6666 -Dresources=./ -classpath release/latest/opencv-412.jar:release/latest/mouse-server.jar Application &
echo $! >>mouse-server.pid
java -Dport=6667 -Dresources=./ -classpath release/latest/opencv-412.jar:release/latest/mouse-server.jar Application &
echo $! >>mouse-server.pid
java -Dport=6668 -Dresources=./ -classpath release/latest/opencv-412.jar:release/latest/mouse-server.jar Application &
echo $! >>mouse-server.pid
java -Dport=6669 -Dresources=./ -classpath release/latest/opencv-412.jar:release/latest/mouse-server.jar Application &
echo $! >>mouse-server.pid
