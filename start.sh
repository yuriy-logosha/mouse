#!/usr/bin/env bash
java target/classes/com/home/mouse/client/Command exit

java target/classes/com/home/mouse/server/Application -Dapple.awt.UIElement=true

