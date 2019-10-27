package com.home.mouse.server.processors;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.opencv.core.Core;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

public class ImageProcessorTest {

    private final BufferedImage pic0 = openFile("00.png");
    private final BufferedImage pic1 = openFile("big-picture.png");
    private final BufferedImage pic2 = openFile("sub-picture.png");
    private final BufferedImage pic3 = openFile("sub-picture3.png");
    private final BufferedImage pic4 = openFile("1.png");
    private final BufferedImage pic5 = openFile("1_tr.png");
    private final BufferedImage pic6 = openFile("_all.png");
    private final BufferedImage pic7 = openFile("_all2.png");
    private final BufferedImage pic8 = openFile("etalon.png");
    private final BufferedImage pic9 = openFile("zadaniya-refresh7.png");
    private final BufferedImage pic10 = openFile("screen-zadanija-get");
    private final BufferedImage pic11 = openFile("zadanije-start");
    private final BufferedImage pic12 = openFile("zadanija-get");

    public ImageProcessorTest() throws IOException {
        System.setProperty("resources", "../resources/");
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
    }

    @Before
    public void setUp() throws Exception {
        //ImageProcessor.printImageRGB(pic0);
        //ImageProcessor.printImage(pic0);
    }

    @Test
    public void containsTest() {
        ImageProcessor processor = new ImageProcessor();
        org.opencv.core.Point result = processor.containsEx(pic1, pic2);
        assertNotNull(result);
        assertEquals(1286, result.x, 0);
        assertEquals(490, result.y, 0);
    }

    @Test
    public void containsOldTest() throws IOException {
        ImageProcessor processor = new ImageProcessor();
//        org.opencv.core.Point result = processor.contains(openFile("screen-zadanija-get"), openFile("zadanije-start"));
//        long start = System.currentTimeMillis();

        org.opencv.core.Point result = processor.containsEx(pic10, pic11);
//        long end = System.currentTimeMillis() - start;
//        System.out.println(end);
        assertNotNull(result);
//        assertEquals(1286, result.x, 0);
//        assertEquals(490, result.y, 0);
    }

    @Test
    public void containsOld2Test() throws IOException {
        ImageProcessor processor = new ImageProcessor();
        org.opencv.core.Point result = processor.containsEx(pic10, "zadanije-start.png");
        assertNotNull(result);
        assertEquals(610.0, result.x, 0);
        assertEquals(766.0, result.y, 0);
    }



    @Ignore
    @Test
    public void containsTestOnSmallPictures() throws IOException {
        ImageProcessor processor = new ImageProcessor();
        org.opencv.core.Point result = processor.containsEx(pic4, "1_tr.png");
        assertNotNull(result);
        assertEquals(2, result.x, 0);
        assertEquals(0, result.y, 0);
    }

    @Ignore
    @Test
    public void containsTestTransparency() throws IOException {
        ImageProcessor processor = new ImageProcessor();
        org.opencv.core.Point result = processor.containsEx(pic6, "1_tr.png");
        assertNotNull(result);
        assertEquals(465, result.x, 0);
        assertEquals(465, result.y, 0);
    }

    @Test
    public void containsTestTransparencyZero() throws IOException {
        ImageProcessor processor = new ImageProcessor();
        org.opencv.core.Point result = processor.containsEx(pic7, pic0);
        assertNotNull(result);
        assertEquals(350, result.x, 0);
        assertEquals(465, result.y, 0);
    }

    @Test
    public void containsExTest() throws IOException {
        ImageProcessor processor = new ImageProcessor();
        org.opencv.core.Point result = processor.containsEx(pic8, pic9);
        assertNotNull(result);
        assertEquals(2, result.x, 0);
        assertEquals(5, result.y, 0);
    }

    @Test
    public void containsImgVsImg() throws IOException {
        ImageProcessor processor = new ImageProcessor();
        org.opencv.core.Point result = processor.containsEx(pic1, pic2);
        assertNotNull(result);
        assertEquals(1286.0, result.x, 0);
        assertEquals(490.0, result.y, 0);
    }


    @Test
    @Ignore
    public void firstMethodLoadTest() {
        ImageProcessor processor = new ImageProcessor();
        int result = 0;
        int cases = 10;
        for (int i = 0; i < cases; i++) {
            Instant start = Instant.now();
            org.opencv.core.Point point = processor.containsEx(pic1, pic3);
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
            org.opencv.core.Point point = processor.containsEx(pic8, pic9);
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
        ImageProcessor.printImageRGB(pic10, 610, 765);
        ImageProcessor.printImageRGB(pic12Team co-located in several locations, 0, 0);
    }


    private BufferedImage openFile(String fileName) throws IOException {
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(fileName);
        if (resourceAsStream != null) {
            return ImageIO.read(resourceAsStream);
        }
        resourceAsStream = getClass().getClassLoader().getResourceAsStream(fileName+".png");
        if (resourceAsStream != null) {
            return ImageIO.read(resourceAsStream);
        }

        return null;
    }

}