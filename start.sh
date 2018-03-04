#!/usr/bin/env bash
java client/target/classes/com/home/mouse/client/Command exit

java server/target/classes/com/home/mouse/server/Application -Dapple.awt.UIElement=true

