package com.home.mouse.server.controller;

import com.home.mouse.server.processors.CommandProcessor;
import com.home.mouse.server.processors.ImageProcessor;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.StringTokenizer;
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
        processor = new CommandProcessor(this, new ImageProcessor());
    }

    public MouseController(Robot robot, int port) {
        this(robot);
        this.port = port;
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

                String line = in.readUTF();

                logger.log(Level.INFO, "Received: {0}", new Object[]{line});

                StringTokenizer tokenizer = new StringTokenizer(line, ";");
                while (tokenizer.hasMoreElements()) {
                    String command = tokenizer.nextToken();
                    String result = processor.process(command.trim());
                    try {
                        out.writeUTF(result);
                        out.writeUTF("Done");
                        out.flush();
                    } catch (SocketException e) {
                        logger.log(Level.FINER, "Exception occurred while sending result back.", e);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
