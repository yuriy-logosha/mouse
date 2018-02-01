package com.home.mouse.server;

import com.home.mouse.server.MouseController;

import java.awt.*;
import java.awt.event.InputEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Mouse {

    public Mouse() throws AWTException {
    }

    public static void main(String[] args) throws AWTException, InterruptedException {
        System.setProperty("apple.awt.UIElement", "true");
        MouseController mouseController = new MouseController(new Robot());
        mouseController.start();
    }
}
