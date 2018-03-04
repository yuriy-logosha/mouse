#!/usr/bin/env bash
declare RESULT=$(java -jar client/target/mouse-client.jar $1 $2 $3 $4)

echo $RESULT