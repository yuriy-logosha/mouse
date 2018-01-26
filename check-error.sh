#!/usr/bin/env bash
cd /Users/yura/IdeaProjects/mouse/target/classes
RESULT="Not found"
while [ "$RESULT" = "Not found" ]; do
    RESULT=$(java com/home/Command contains src/test/resources/connection-lost.png)
    if [ "$RESULT" != "Not found" ]; then
        _RESULT=($RESULT)
        x=${_RESULT[0]}
        y=${_RESULT[1]}
        x=$(($x + 100))
        y=$(($y + 50))
        RESULT=$(java com/home/Move ${x} ${y})
        RESULT=$(java com/home/Command lclick)
        break
    fi
done

exit 0

