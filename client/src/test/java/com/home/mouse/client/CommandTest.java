package com.home.mouse.client;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.ReaderInputStream;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CommandTest {

    private boolean isExit;
    private int port = 6666;

    private String result;
    private final String etalon = "move 10 10";

    private Command client;
    private Socket socket;
    private OutputStream sOut;


    @Before
    public void setUp() throws Exception {
        socket = mock(Socket.class);

        sOut = mock(OutputStream.class);
        doReturn(sOut).when(socket).getOutputStream();

        StringReader reader = new StringReader(etalon);
        InputStream fakeStream = new ReaderInputStream(reader);
        doReturn(fakeStream).when(socket).getInputStream();
        //IOUtils.toInputStream(etalon, "UTF-8")
        client = new Command();
    }

//    @Test
    public void execute() throws IOException {
        client.send(socket, new String[]{"move", "10", "10"});
        //assertTrue(etalon.equals(result));
        verify(sOut, times(1)).write(any(byte[].class), any(int.class), any(int.class));
    }

    //
    // Helpers
    //

}