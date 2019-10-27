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

    private static Robot robot;
    private boolean isExit = false;
    private int port = -1;

    private CommandProcessor processor;

    public MouseController(Robot robot, int port) {
        this.robot = robot;
        processor = new CommandProcessor(this, new ImageProcessor());
        this.port = port;
        if (port < 1000 || port > 65535) {
            throw new IllegalArgumentException("port value must be between 1000 - 65535");
        }
    }

    public static Robot getRobot(){
        return robot;
    }

    public void exit(){
        this.isExit = true;
    }

    public void start() {
        try {
            ServerSocket ss = new ServerSocket(port);
            logger.log(Level.INFO, "Waiting for a command on port {0}", String.valueOf(port));

            while (!isExit) {
                Socket socket = ss.accept();

                InputStream sin = socket.getInputStream();
                OutputStream sout = socket.getOutputStream();

                DataInputStream in = new DataInputStream(sin);
                DataOutputStream out = new DataOutputStream(sout);

                String line = in.readUTF().replaceAll("\u0000", "");

                logger.log(Level.FINE, "Received: {0}", line);

                StringTokenizer tokenizer = new StringTokenizer(line, ";");
                while (tokenizer.hasMoreElements()) {
                    out.writeUTF(processor.process(tokenizer.nextToken().trim()));
                }
                try {
                    out.writeUTF("Done");
                    out.flush();
                } catch (IOException e) {
                    logger.log(Level.FINER, "Exception occurred while sending result back.", e);
                }
            }
        } catch (IOException e) {
            logger.log(Level.FINER, "General exception: ", e);
        }
    }
}
