#!/usr/bin/env bash
declare RESULT=$(java -jar release/latest/mouse-client.jar $1 $2 $3 $4 $5 $6 $7 $8 $9 $10 $11 $12)

echo $RESULT