package com.home.mouse.server.controller;

import com.home.mouse.server.processors.CommandProcessor;

import java.awt.*;
import java.io.*;
import java.net.*;

public class MouseController {
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
            System.out.println("Waiting for a command...");

            while (!isExit) {
                Socket socket = ss.accept();

                InputStream sin = socket.getInputStream();
                OutputStream sout = socket.getOutputStream();

                DataInputStream in = new DataInputStream(sin);
                DataOutputStream out = new DataOutputStream(sout);

                String line = null;

                try {
                    line = in.readUTF();

                    System.out.println("Received: " + line);

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
        } catch (IOException x) {
            System.out.println("Can not create listener on port " + port);
            x.printStackTrace();
        }
    }
}
