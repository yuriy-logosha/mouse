package com.home.mouse.server.processors;

import com.home.mouse.server.controller.MouseController;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.io.IOException;

import static org.junit.Assert.*;

public class CommandProcessorTest {

    private MouseController mc;
    private Robot robor;
    private CommandProcessor cp;

    @Before
    public void setUp() throws AWTException {
        robor = new Robot();
        mc = new MouseController(robor);
        cp = new CommandProcessor(mc);
    }

    @Test
    public void process() throws IOException, AWTException {
        cp.process("move 0 0");
        Assert.assertTrue(MouseInfo.getPointerInfo().getLocation().getX() == 0);
    }
}