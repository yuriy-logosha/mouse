#!/usr/bin/env bash
declare RESULT=$(java -jar target/exec.jar $1 $2 $3 $4)

echo $RESULT