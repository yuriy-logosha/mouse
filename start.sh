#!/usr/bin/env bash

#java -jar client/target/mouse-client.jar exit

java -Djava.util.logging.config.file=server/src/main/resources/logging.properties -Dapple.awt.UIElement=true -jar server/target/mouse-server.jar

#java -jar server/target/mouse-server.jar

