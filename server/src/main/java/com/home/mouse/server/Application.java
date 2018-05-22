package com.home.mouse.server;

import com.home.mouse.server.controller.MouseController;

import java.awt.*;

public class Application {

    public static void main(String[] args) throws AWTException {
        System.setProperty("apple.awt.UIElement", "true");
        String port = System.getProperty("port");
        MouseController mouseController;
        if(port != null && !port.isEmpty()) {
            mouseController = new MouseController(new Robot(), Integer.valueOf(port));
        } else {
            mouseController = new MouseController(new Robot());
        }
        mouseController.start();
    }

}
