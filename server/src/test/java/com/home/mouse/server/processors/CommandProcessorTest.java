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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class CommandProcessorTest {

    private MouseController mc;
    private Robot robor;
    private CommandProcessor cp;
    private ImageProcessor ip;

    @Before
    public void setUp() throws AWTException {
        robor = new Robot();
        mc = new MouseController(robor);
        ip = mock(ImageProcessor.class);
        when(ip.contains(any(BufferedImage.class), any(BufferedImage.class))).thenReturn(null);
        cp = new CommandProcessor(mc, ip);
    }

    @Test
    @Ignore
    public void process() throws IOException, AWTException {
        cp.process("move 0 0");
        Assert.assertTrue(MouseInfo.getPointerInfo().getLocation().getX() == 0);
    }

    @Test
    @Ignore
    public void testTransparentPicture () throws IOException {
        BufferedImage bi = getResource("1_tr.png");
        for(int i = 0; i <= bi.getHeight()-1; i++) {
            for(int j = 0; j <= bi.getWidth()-1; j++) {
                System.out.println(bi.getRGB(j, i));
            }
        }
    }

    @Test
    public void testShow () throws IOException, AWTException {
        BufferedImage bi = getResource("1_tr.png");
        String result = cp.process("show");
        Assert.assertTrue(result.split(" ").length == 2);
    }

    @Test
    public void testContainsAll () throws IOException, AWTException {
        BufferedImage bi = getResource("1_tr.png");
        String result = cp.process("containsAll src/test/resources/sub-picture.png src/test/resources/sub-picture2.png src/test/resources/sub-picture3.png");
        Assert.assertTrue(result.equals("Not found"));
    }

    @Test
    public void testContainsAllException () throws IOException, AWTException {
        ImageProcessor ip = mock(ImageProcessor.class);
        when(ip.contains(any(BufferedImage.class), any(BufferedImage.class))).thenReturn(null);
        when(ip.contains(any(BufferedImage.class), any(BufferedImage[].class))).thenCallRealMethod();

        CommandProcessor cp = new CommandProcessor(mc, ip);

        String result = cp.process("containsAll src/test/resources/sub-picture.png src/test/resources/sub-picture2.png src/test/resources/sub-picture3.png");
        Assert.assertTrue(result.equals("Not found"));
        verify(ip, times(1)).contains(any(BufferedImage.class), any(BufferedImage[].class));
        verify(ip, times(3)).contains(any(BufferedImage.class), any(BufferedImage.class));
    }

    private static BufferedImage getResource(String name) throws IOException {
        return ImageIO.read(CommandProcessorTest.class.getClassLoader().getResourceAsStream(name));
    }

}