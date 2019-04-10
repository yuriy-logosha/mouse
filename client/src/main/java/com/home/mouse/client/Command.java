package com.home.mouse.client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

public class Command {

    public static String send(Socket socket, String[] args) throws IOException {

        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        out.writeUTF(String.join(" ", args));
        out.flush();
        String result = "";
        InputStream in = socket.getInputStream();
        DataInputStream din = new DataInputStream(in);
        try {
            while (true) {
                String portion = din.readUTF();
                if ("Done".equals(portion)){
                    return result;
                }
                result += portion;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }

    }

    public static void main(String[] args) throws IOException {
        final String address = "127.0.0.1";
        final int serverPort = 6666;
        final Socket socket = new Socket(InetAddress.getByName(address), serverPort);
        System.out.printf(Command.send(socket, args));
    }
}
