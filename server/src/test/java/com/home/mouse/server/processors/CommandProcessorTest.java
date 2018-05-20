package com.home.mouse.server.processors;

import com.home.mouse.server.controller.MouseController;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
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
    @Ignore
    public void process() throws IOException, AWTException {
        cp.process("move 0 0");
        Assert.assertTrue(MouseInfo.getPointerInfo().getLocation().getX() == 0);
    }

    @Test
    public void testTransparentPicture () throws IOException {
        BufferedImage bi = ImageIO.read(getClass().getClassLoader().getResourceAsStream("1_tr.png"));
        for(int i = 0; i <= bi.getHeight()-1; i++) {
            for(int j = 0; j <= bi.getWidth()-1; j++) {
                System.out.println(bi.getRGB(j, i));
            }
        }
    }
}