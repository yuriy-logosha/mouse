package com.home;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Command {
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

            StringBuilder sb = new StringBuilder();
            for (String arg : args) {
                sb.append(arg);
                sb.append(" ");
            }

            out.writeUTF(sb.toString());
            out.flush();
            System.out.printf(in.readUTF());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
