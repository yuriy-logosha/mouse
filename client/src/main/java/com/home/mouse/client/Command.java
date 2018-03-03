package com.home.mouse.client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Command {

    public static String send(Socket socket, String[] args) throws IOException {
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        out.writeUTF(arrToString(args));
        out.flush();
        return (new DataInputStream(socket.getInputStream())).readUTF();
    }

    public static void main(String[] args) throws IOException {
        final String address = "127.0.0.1";
        final int serverPort = 6666;
        final Socket socket = new Socket(InetAddress.getByName(address), serverPort);
        System.out.printf(Command.send(socket, args));
    }

    //
    // Helpers
    //

    private static String arrToString(String[] arr){
        StringBuilder sb = new StringBuilder();
        for (String arg : arr) {
            sb.append(arg);
            sb.append(" ");
        }
        return sb.toString();
    }
}
