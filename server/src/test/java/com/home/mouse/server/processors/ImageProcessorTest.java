package com.home.mouse.server.processors;

import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

public class ImageProcessorTest {

    private BufferedImage pic1;
    private BufferedImage pic2;
    private BufferedImage pic3;

    @Before
    public void setUp() throws Exception {
        pic1 = ImageIO.read(getClass().getClassLoader().getResourceAsStream("big-picture.png"));
        pic2 = ImageIO.read(getClass().getClassLoader().getResourceAsStream("sub-picture.png"));
        pic3 = ImageIO.read(getClass().getClassLoader().getResourceAsStream("sub-picture3.png"));

    }

    @Test
    public void containsTest() {
        Point result = ImageProcessor.contains(pic1, pic2);
        assertNotNull(result);
        assertTrue(result.getX() == 1286.0);
        assertTrue(result.getY() == 490.0);
    }

    @Test
    public void containsExTest() {
        Point result = ImageProcessor.containsEx(pic1, pic2);
        assertNotNull(result);
        assertTrue(result.getX() == 1286.0);
        assertTrue(result.getY() == 490.0);
    }

    @Test
    public void firstMethodTest() {
        int result = 0;
        int cases = 20;
        for (int i = 0; i < cases; i++) {
            Instant start = Instant.now();
            Point point = ImageProcessor.contains(pic1, pic3);
            Duration between = Duration.between(start, Instant.now());
            result += between.toMillis();
            assertNotNull(point);
        }
        System.out.println("Average for first method (ms): " + result/cases);
        assertTrue(result/cases <= 900);
    }

    @Test
    public void secondMethodTest() {
        int result = 0;
        int cases = 20;
        for (int i = 0; i < cases; i++) {
            Instant start = Instant.now();
            Point point = ImageProcessor.containsEx(pic1, pic3);
            Duration between = Duration.between(start, Instant.now());
            result += between.toMillis();
            assertNotNull(point);
        }
        System.out.println("Average for second method (ms): " + result/cases);
        assertTrue(result/cases <= 900);
    }

}