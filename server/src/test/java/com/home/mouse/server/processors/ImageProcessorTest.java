package com.home.mouse.server.processors;

import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

public class ImageProcessorTest {

    private BufferedImage pic1;
    private BufferedImage pic2;
    private BufferedImage pic3;
    private BufferedImage pic4;
    private BufferedImage pic5;
    private BufferedImage pic6;
    private BufferedImage pic7;
    private BufferedImage pic8;
    private BufferedImage pic9;
    private BufferedImage pic0;

    @Before
    public void setUp() throws Exception {
        pic1 = ImageIO.read(getClass().getClassLoader().getResourceAsStream("big-picture.png"));
        pic2 = ImageIO.read(getClass().getClassLoader().getResourceAsStream("sub-picture.png"));
        pic3 = ImageIO.read(getClass().getClassLoader().getResourceAsStream("sub-picture3.png"));
        pic4 = ImageIO.read(getClass().getClassLoader().getResourceAsStream("1.png"));
        pic5 = ImageIO.read(getClass().getClassLoader().getResourceAsStream("1_tr.png"));
        pic6 = ImageIO.read(getClass().getClassLoader().getResourceAsStream("_all.png"));
        pic7 = ImageIO.read(getClass().getClassLoader().getResourceAsStream("_all2.png"));
        pic0 = ImageIO.read(getClass().getClassLoader().getResourceAsStream("00.png"));
        pic8 = ImageIO.read(getClass().getClassLoader().getResourceAsStream("case1.png"));
        pic9 = ImageIO.read(getClass().getClassLoader().getResourceAsStream("rss-number-0.png"));
        //ImageProcessor.printImageRGB(pic0);
        //ImageProcessor.printImage(pic0);

    }

    @Test
    public void containsTest1() {
        Point result = ImageProcessor.contains(pic8, pic9);
        //assertNotNull(result);
    }


    @Test
    public void containsTest() {
        Point result = ImageProcessor.contains(pic1, pic2);
        assertNotNull(result);
        assertEquals(1286, result.getX(), 0);
        assertEquals(490, result.getY(), 0);
    }

    @Test
    public void containsTestOnSmallPictures() {
        Point result = ImageProcessor.contains(pic4, pic5);
        assertNotNull(result);
        assertEquals(2, result.getX(), 0);
        assertEquals(0, result.getY(), 0);
    }

    @Test
    public void containsTestTransparency() {
        Point result = ImageProcessor.contains(pic6, pic5);
        assertNotNull(result);
        assertEquals(349, result.getX(), 0);
        assertEquals(466, result.getY(), 0);
    }

    @Test
    public void containsTestTransparencyZero() {
        Point result = ImageProcessor.contains(pic7, pic0);
        assertNotNull(result);
        assertEquals(350, result.getX(), 0);
        assertEquals(465, result.getY(), 0);
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

    @Test
    public void printImage() throws IOException {
        ImageProcessor.printImageRGB(pic7);
        ImageProcessor.printImageRGB(pic0);
    }

}