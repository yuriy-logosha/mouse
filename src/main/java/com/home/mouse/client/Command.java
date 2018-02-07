package com.home.mouse.client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Command {
    private final static String address = "127.0.0.1";
    private final static int serverPort = 6666;

    private final Socket socket;

    private final DataOutputStream out;
    private final DataInputStream in;

    public Command() throws IOException {
        InetAddress ipAddress = InetAddress.getByName(address);
        socket = new Socket(ipAddress, serverPort);

        InputStream sin = socket.getInputStream();
        OutputStream sout = socket.getOutputStream();

        in = new DataInputStream(sin);
        out = new DataOutputStream(sout);

    }

    public String execute(String[] args) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            sb.append(arg);
            sb.append(" ");
        }

        out.writeUTF(sb.toString());
        out.flush();
        return in.readUTF();
    }

    public static void main(String[] args) throws IOException {
        Command c = new Command();
        System.out.printf(c.execute(args));
    }
}
