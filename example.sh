#!/usr/bin/env bash
cd /Users/yura/IdeaProjects/mouse/target/classes

RESULT=$(java com/home/Command presskey 66)
boosts_window=0

RESULT="Not found"
while [ "$RESULT" = "Not found" ]; do
    RESULT=$(java com/home/Command contains src/test/resources/boosts-close.png)
    if [ "$RESULT" != "Not found" ]; then
        boosts_window=1
        break
    fi
done

if [ "$boosts_window" = 1 ]; then
    RESULT="Not found"
    while [ "$RESULT" = "Not found" ]; do
        RESULT=$(java com/home/Command contains src/test/resources/shield.png)
        if [ "$RESULT" != "Not found" ]; then
            _RESULT=($RESULT)
            x=${_RESULT[0]}
            y=${_RESULT[1]}
            RESULT=$(java com/home/Move ${x} ${y})
            RESULT=$(java com/home/Command lclick)
            break
        fi
    done
fi
exit 0

