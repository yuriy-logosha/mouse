package com.home.mouse.server;

import com.home.mouse.server.processors.CommandProcessor;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TemplatesCache {
    private static final Logger logger = Logger.getLogger(CommandProcessor.class.getName());

    private static Map<String, Mat> cache = new HashMap();

    public static void clear() {
        cache.clear();
    }

    public static Mat getImage(String imgName) {
        Mat img = cache.get(imgName);

        if (img == null) {
            try {
                img = Imgcodecs.imread(Configuration.RESOURCES + imgName, Imgcodecs.IMREAD_COLOR);
                if (img.cols() == 0) {
                    throw new IllegalArgumentException();
                }
                cache.put(imgName, img);
            } catch (Exception e) {
                logger.log(Level.FINER, "File doesn't exists.", imgName);
            }
        }

        return img;
    }

    public static int size() {
        return cache.size();
    }
}
