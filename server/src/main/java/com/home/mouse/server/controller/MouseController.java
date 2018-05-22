package com.home.mouse.server.controller;

import com.home.mouse.server.processors.CommandProcessor;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MouseController {
    private final static Logger logger = Logger.getLogger(MouseController.class.getName());

    private Robot robot;
    private boolean isExit = false;
    private int port = 6666;

    private CommandProcessor processor;

    public MouseController(Robot robot) {
        this.robot = robot;
        processor = new CommandProcessor(this);
    }

    public Robot getRobot(){
        return robot;
    }

    public void exit(){
        this.isExit = true;
    }

    public void start() {
        try {
            ServerSocket ss = new ServerSocket(port);
            logger.log(Level.INFO, "Waiting for a command...");

            while (!isExit) {
                Socket socket = ss.accept();

                InputStream sin = socket.getInputStream();
                OutputStream sout = socket.getOutputStream();

                DataInputStream in = new DataInputStream(sin);
                DataOutputStream out = new DataOutputStream(sout);

                String line = null;

                try {
                    line = in.readUTF();

                    logger.log(Level.INFO, "Received: {0}", new Object[]{line});

                    if (line.contains(";")) {
                        String[] commands = line.split(";");
                        for (String command : commands) {
                            out.writeUTF(processor.process(command.trim()));
                        }
                    } else {
                        out.writeUTF(processor.process(line));
                    }
                    out.writeUTF("Done");
                    out.flush();

                } catch (IOException | NumberFormatException | AWTException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
