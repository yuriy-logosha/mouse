#!/usr/bin/env bash
declare RESULT=$(java -jar release/latest/mouse-client.jar move $1 $2)

echo $RESULT
java -jar release/latest/mouse-client.jar dblclick 200