package com.home;

import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Move {
    public static void main(String[] args) {
        try {
            String address = "127.0.0.1";
            int serverPort = 6666;
            InetAddress ipAddress = InetAddress.getByName(address);
            Socket socket = new Socket(ipAddress, serverPort);

            InputStream sin = socket.getInputStream();
            OutputStream sout = socket.getOutputStream();

            DataInputStream in = new DataInputStream(sin);
            DataOutputStream out = new DataOutputStream(sout);

            out.writeUTF("move " + args[0] + " " + args[1]);
            out.flush();
            System.out.printf(in.readUTF());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
