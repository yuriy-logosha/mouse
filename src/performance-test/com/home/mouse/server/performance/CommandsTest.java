package com.home.mouse.server.performance;

import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

import static org.junit.Assert.assertTrue;

public class CommandsTest {

    private final static String address = "127.0.0.1";
    private final static int serverPort = 6666;
    private Socket socket;

    private DataOutputStream out;
    private DataInputStream in;

    @Before
    public void setUp() throws Exception {
        InetAddress ipAddress = InetAddress.getByName(address);
        socket = new Socket(ipAddress, serverPort);

        InputStream sin = socket.getInputStream();
        OutputStream sout = socket.getOutputStream();

        in = new DataInputStream(sin);
        out = new DataOutputStream(sout);
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
        long start = System.currentTimeMillis();
        repeateCommand(1000, "contains server/src/test/resources/sub-picture2.png");
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
        try {
            out.writeUTF(command);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
