#!/bin/bash
declare RESULT=$(java -jar release/latest/mouse-client.jar contains $1)

echo $RESULT

if [[ $RESULT != *found* ]];
then
  IFS=' ' # space is set as delimiter
  read -ra XY <<< "$RESULT"
  echo "Found!" ${XY[0]} ${XY[1]}
fi
