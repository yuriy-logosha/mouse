package com.home.mouse.server.processors;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.PriorityQueue;

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
    private BufferedImage pic0;
    private BufferedImage pic8;
    private BufferedImage pic9;

    @Before
    public void setUp() throws Exception {
        pic0 = openFile("00.png");
        pic1 = openFile("big-picture.png");
        pic2 = openFile("sub-picture.png");
        pic3 = openFile("sub-picture3.png");
        pic4 = openFile("1.png");
        pic5 = openFile("1_tr.png");
        pic6 = openFile("_all.png");
        pic7 = openFile("_all2.png");
        pic8 = openFile("etalon.png");
        pic9 = openFile("zadaniya-refresh7.png");
        //ImageProcessor.printImageRGB(pic0);
        //ImageProcessor.printImage(pic0);

    }

    @Test
    public void containsTest() {
        ImageProcessor processor = new ImageProcessor();
        Point result = processor.contains(pic1, pic2);
        assertNotNull(result);
        assertEquals(1286, result.getX(), 0);
        assertEquals(490, result.getY(), 0);
    }

    @Test
    public void containsTestOnSmallPictures() {
        ImageProcessor processor = new ImageProcessor();
        Point result = processor.contains(pic4, pic5);
        assertNotNull(result);
        assertEquals(2, result.getX(), 0);
        assertEquals(0, result.getY(), 0);
    }

    @Test
    public void containsTestTransparency() {
        ImageProcessor processor = new ImageProcessor();
        Point result = processor.contains(pic6, pic5);
        assertNotNull(result);
        assertEquals(465, result.getX(), 0);
        assertEquals(465, result.getY(), 0);
    }

    @Test
    public void containsTestTransparencyZero() {
        ImageProcessor processor = new ImageProcessor();
        Point result = processor.contains(pic7, pic0);
        assertNotNull(result);
        assertEquals(350, result.getX(), 0);
        assertEquals(465, result.getY(), 0);
    }

    @Test
    public void containsExTest() throws IOException {
        ImageProcessor processor = new ImageProcessor();
        Point result = processor.containsEx(pic8, pic9);
        assertNotNull(result);
        assertEquals(5, result.getX(), 0);
        assertEquals(2, result.getY(), 0);
    }

    @Test
    @Ignore
    public void firstMethodLoadTest() {
        ImageProcessor processor = new ImageProcessor();
        int result = 0;
        int cases = 10;
        for (int i = 0; i < cases; i++) {
            Instant start = Instant.now();
            Point point = processor.contains(pic1, pic3);
            Duration between = Duration.between(start, Instant.now());
            result += between.toMillis();
            assertNotNull(point);
        }
        System.out.println("Average for first method (ms): " + result/cases);
        assertTrue(result/cases <= 900);
    }

    @Test
    @Ignore
    public void secondMethodLoadTest() throws IOException {
        ImageProcessor processor = new ImageProcessor();
        int result = 0;
        int cases = 10;
        for (int i = 0; i < cases; i++) {
            Instant start = Instant.now();
            Point point = processor.containsEx(pic8, pic9);
            Duration between = Duration.between(start, Instant.now());
            result += between.toMillis();
            assertNotNull(point);
        }
        System.out.println("Average for second method (ms): " + result/cases);
        assertTrue(result/cases <= 900);
    }

    @Test
    @Ignore
    public void printImage() throws IOException {
        ImageProcessor.printImageRGB(pic7);
        ImageProcessor.printImageRGB(pic0);
    }


    private BufferedImage openFile(String fileName) throws IOException {
        return ImageIO.read(getClass().getClassLoader().getResourceAsStream(fileName));
    }

}