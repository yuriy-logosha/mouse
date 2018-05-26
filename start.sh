#!/usr/bin/env bash
java -jar client/target/mouse-client.jar exit

java -jar server/target/mouse-server.jar -Djava.util.logging.config.file=server/src/main/resources/logging.properties -Dapple.awt.UIElement=true

