#!/bin/bash
declare RESULT=$(java -jar release/latest/mouse-client.jar contains resources/$1.png)

if [[ $RESULT != *found* ]];
then
  IFS=' ' # space is set as delimiter
  read -ra XY <<< "$RESULT"
  bash click.sh ${XY[0]} ${XY[1]}
else
  echo $RESULT
fi
