package com.home.mouse.server.performance;

import com.home.mouse.client.Command;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CommandsTest {

    private final static String address = "127.0.0.1";
    private final static int serverPort = 6666;
    private InetAddress ipAddress;
    @Before
    public void setUp() throws Exception {
        ipAddress = InetAddress.getByName(address);
    }

    @Test
    public void moveCursor10Times() {
        long start = System.currentTimeMillis();
        repeateCommand(10, "move 10 10");
        long end = System.currentTimeMillis() - start;
        assertTrue(end <= 100);
    }

    @Test
    public void moveCursor100Times() {
        long start = System.currentTimeMillis();
        repeateCommand(100, "move 10 10");
        long end = System.currentTimeMillis() - start;
        assertTrue(end <= 200);
    }

    @Test
    public void moveCursor1000Times() {
        long start = System.currentTimeMillis();
        repeateCommand(1000, "move 10 10");
        long end = System.currentTimeMillis() - start;
        assertTrue(end <= 300);
    }


    @Test
    public void executeShow1000Times() {
        long start = System.currentTimeMillis();
        repeateCommand(1000, "show");
        long end = System.currentTimeMillis() - start;
        assertTrue(end <= 300);
    }

    @Test
    public void contains10Times() {
        sendCommand("screenRange2File screen.png 10 10 20 20");
        long start = System.currentTimeMillis();
        repeateCommand(10, "contains screen.png");
        long end = System.currentTimeMillis() - start;
        assertTrue(end <= 2000);
    }

    @Test
    public void screenRange2File() {
        long start = System.currentTimeMillis();
        sendCommand("screenRange2File screen.png 10 10 20 20");
        long end = System.currentTimeMillis() - start;
        assertTrue(end <= 300);
    }

    //
    // Helpers
    //

    private void repeateCommand(int times, String command) {
        for (int i = 0; i < times; i++) {
            sendCommand(command);
        }
    }

    private void sendCommand(String command) {
        Socket sct = null;
        try {
            sct = new Socket(ipAddress, serverPort);
            Command.send(sct, command.split(" "));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (sct != null) {
                try {
                    sct.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
