package com.home.mouse.server.processors;

import java.awt.*;
import java.awt.image.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.home.mouse.server.TemplatesCache;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;

import static java.lang.Math.round;

public class ImageProcessor {

    private final static Logger logger = Logger.getLogger(ImageProcessor.class.getName());

    Boolean use_mask = false;
    Mat img = new Mat(), templ = new Mat();
    Mat mask = new Mat();

    private static Map<String, BufferedImage> cache = new HashMap();

    int match_method = Imgproc.TM_SQDIFF;
    Boolean method_accepts_mask = (Imgproc.TM_SQDIFF == match_method || match_method == Imgproc.TM_CCORR_NORMED);

    public org.opencv.core.Point contains(BufferedImage leftImage, String[] images) throws IOException {
        for (String image : images) {
            org.opencv.core.Point point = containsEx(leftImage, image);
            if (point != null) {
                return point;
            }
        }
        return null;
    }

    public org.opencv.core.Point contains(BufferedImage leftImage, BufferedImage[] images) throws IOException {
        for (BufferedImage image : images) {
            org.opencv.core.Point point = contains(leftImage, image);
            if (point != null) {
                return point;
            }
        }
        return null;
    }

    public org.opencv.core.Point contains(BufferedImage leftImage, BufferedImage rightImage) {
        Objects.requireNonNull(leftImage);
        Objects.requireNonNull(rightImage);
        XYV notEmptyValue1 = getFirstNotZeroedValue(rightImage);

        for (int hY = 0; hY < leftImage.getHeight(); hY++) {
            for (int hX = 0; hX < leftImage.getWidth(); hX++) {

                XYV leftValue = buildXYV(leftImage, hX, hY);

                if (leftValue.isSimilar(notEmptyValue1)) {
                    if (isPictureMatch(notEmptyValue1, leftImage, rightImage, hX, hY)) {
                        return new org.opencv.core.Point(hX, hY);
                    }
                }
            }
        }

        return null;
    }

    private boolean isPictureMatch(XYV xyv, BufferedImage leftImage, BufferedImage rightImage, int hX, int hY) {
        int skippedLimit = (rightImage.getHeight() * rightImage.getWidth())/10;
        int skipped = 0;
        int shiftX = hX - xyv.x;
        int shiftY = hY - xyv.y;
        for (int nY = xyv.y; nY < rightImage.getHeight(); nY++) {
            for (int nX = xyv.x; nX < rightImage.getWidth() && rightImage.getRGB(nX, nY) != 0; nX++) {
                if(leftImage.getWidth() -1 < nX + shiftX || leftImage.getHeight() -1 < nY + shiftY) {
                    return false;
                }
                XYV leftValue = buildXYV(leftImage,nX + shiftX, nY + shiftY);

                XYV rightValue = getAddition(rightImage, nX, nY);

                if (!leftValue.isSimilar(rightValue)) {
                    if (skippedLimit > skipped) {
                        skipped +=1;
                    } else {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    Map<String, XYV> additionsMap = new HashMap();

    private XYV getAddition(BufferedImage rightImage, int nX, int nY) {
        String key = nX+":"+nY;
        XYV v = additionsMap.get(key);
        if (v != null) {
            return v;
        }
        v = buildXYV(rightImage, nX, nY);
        additionsMap.put(key, v);
        return v;
    }

    private XYV getFirstNotZeroedValue(BufferedImage rightImage) {
        for (int y = 0; y < rightImage.getHeight(); y++) {
            for (int x = 0; x < rightImage.getWidth() && rightImage.getRGB(y, x) != 0; x++) {
                return getAddition(rightImage, y, x);
            }
        }
        return null;
    }

    private XYV buildXYV(BufferedImage img, int x, int y) {
        try {
            Object dataElements = img.getRaster().getDataElements(x, y, null);
            int value1 = img.getColorModel().getRed(dataElements);
            int value2 = img.getColorModel().getGreen(dataElements);
            int value3 = img.getColorModel().getBlue(dataElements);
            return new XYV(x, y, img.getRGB(x, y), new RGB(value1, value2, value3));
        } catch(ArrayIndexOutOfBoundsException e) {
            System.out.println("Error creating XYV from: " + x + " " + y);
            e.printStackTrace();
        }
        return null;
    }

    public org.opencv.core.Point containsEx(BufferedImage bigImage, BufferedImage subImage) {

        List<XYV> dots = analyzeImage(subImage);

        for (int hY = 0; hY < bigImage.getHeight() - subImage.getHeight(); hY++) {
            for (int hX = 0; hX < bigImage.getWidth() - subImage.getWidth(); hX++) {
                if (checkKeyPoints(dots, bigImage, hX, hY)) {
                    return new org.opencv.core.Point(hX, hY);
                }
            }
        }
        return null;
    }

    private List<XYV> analyzeImage(BufferedImage subImage) {
        List<XYV> ldots = new ArrayList();


//        int incY = Math.round(subImage.getHeight()/10);
//        int incX = Math.round(subImage.getWidth()/10);
        for(int y = 0; y < subImage.getHeight(); y+=1) {
            for(int x = 0; x < subImage.getWidth(); x+=1) {
                int value = subImage.getRGB(x, y);
                if(value != 0) {
                    ldots.add(buildXYV(subImage, x, y));
                }
            }
        }

        return ldots;

//        XYV[] result = new XYV[ldots.size()];
//        for (int idx = 0; idx < ldots.size(); idx++) {
//            result[idx] = ldots.get(idx);
//        }
//
//        return result;
    }

    private boolean checkKeyPoints(List<XYV> dots, BufferedImage bigImage, int hX, int hY) {
        XYV zero = dots.get(0);
        XYV xyv;
        int shiftX = hX - zero.x;
        int shiftY = hY - zero.y;


        for (XYV v : dots) {
            xyv = buildXYV(bigImage, v.x + shiftX, v.y + shiftY);
            if(!xyv.rgb.isSimilar(v.rgb)){
                return false;
            }
            System.out.println(shiftX + " " + shiftY + " " + xyv.rgb);
            System.out.println(shiftX + " " + shiftY + " " + v.rgb);
        }
        return true;
    }

    public static void printImageRGB(BufferedImage image, int startX, int startY) {
        int maxH = 20;
        int maxW = 80;
        ColorModel colorModel = image.getColorModel();
        Raster raster = image.getRaster();

        System.out.println("hasAlpha: " + image.getColorModel().hasAlpha());
        System.out.println("isAlphaPremultiplied: " + image.getColorModel().isAlphaPremultiplied());
        System.out.println("getTransparency: " + colorModel.getTransparency());

        System.out.print("____");

        for (int hX = startX; hX < getMin(image.getWidth(), maxW + startX); hX++) {
            System.out.print(("         " + hX + " ").substring(String.valueOf(hX).length()));
        }
        System.out.println("|");
        for (int hY = startY; hY < getMin(image.getHeight(), maxH+startY); hY++) {
            System.out.print(("000" + hY).substring(String.valueOf(hY).length()) + "|");
            for (int hX = startX; hX < ((image.getWidth() >= maxW + startX) ? maxW + startX : image.getWidth()); hX++) {
                Object dataElements = raster.getDataElements(hX, hY, null);
                int r = colorModel.getRed(dataElements);
                int g = colorModel.getGreen(dataElements);
                int b = colorModel.getBlue(dataElements);
                int a = colorModel.getAlpha(dataElements);

                if(r == 0 && g == 0 && b == 0) {
                    System.out.print("          ");
                } else {
                    System.out.print(getRGB(colorModel, dataElements) + " ");
                }
            }
            System.out.println("|");
        }
    }

    private static final String getRGB(ColorModel colorModel, Object dataElements) {
        int r = colorModel.getRed(dataElements);
        int g = colorModel.getGreen(dataElements);
        int b = colorModel.getBlue(dataElements);
        return toFormat(r) + toFormat(g) + toFormat(b);
    }

    private static final String getRGB(RGB rgb) {
        int r = rgb.r;
        int g = rgb.g;
        int b = rgb.b;
        return toFormat(r) + toFormat(g) + toFormat(b);
    }

    private static int getMin(int left, int right) {
        return (right >= left)? left:right;
    }

    private static String toFormat(int val) {
        return String.format("%03d", val);
    }
    public static void printImage(BufferedImage image) {
        System.out.print("____");
        for (int hX = 0; hX < image.getWidth(); hX++) {
            System.out.print(("         " + hX + " ").substring(String.valueOf(hX).length()));
        }
        System.out.println("|");
        for (int hY = 0; hY < image.getHeight(); hY++) {
            System.out.print(("000" + hY).substring(String.valueOf(hY).length()) + "|");
            for (int hX = 0; hX < image.getWidth(); hX++) {
                String value = String.valueOf(image.getRGB(hX, hY));
                if (image.getRGB(hX, hY) == 0) {
                    System.out.print("          ");
                } else {
                    System.out.print(("         " + value).substring(value.length()) + " ");
                }
            }
            System.out.println("|");
        }
    }

    public static void printImage(BufferedImage image, Rectangle rectangle) {
        System.out.print("____");
        for (int hX = 0; hX < rectangle.getWidth(); hX++) {
            System.out.print(("         " + hX + " ").substring(String.valueOf(hX).length()));
        }
        System.out.println("|");
        for (int hY = 0; hY < rectangle.getHeight(); hY++) {
            System.out.print(("000" + hY).substring(String.valueOf(hY).length()) + "|");
            for (int hX = 0; hX < rectangle.getWidth(); hX++) {
                String value = String.valueOf(image.getRGB(hX, hY));
                if (image.getRGB(hX, hY) == 0) {
                    System.out.print("          ");
                } else {
                    System.out.print(("         " + value).substring(value.length()) + " ");
                }
            }
            System.out.println("|");
        }
    }

    public org.opencv.core.Point containsEx(BufferedImage screen, String fileName) throws IOException {
        img = bufferedImage2Mat(screen);
        templ = TemplatesCache.getImage(fileName);
        Objects.requireNonNull(img);
        Objects.requireNonNull(templ);
        return matchingMethod(fileName);
    }

    public static Mat bufferedImage2Mat(BufferedImage image) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", byteArrayOutputStream);
        byteArrayOutputStream.flush();
        return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.IMREAD_UNCHANGED);
    }

    public static BufferedImage getImage(String imgName) {
        BufferedImage img = cache.get(imgName);

        if (img == null) {
            try {
                img = ImageIO.read(new File(imgName));
                cache.put(imgName, img);
            } catch (IOException e) {
                logger.log(Level.FINER, "File doesn't exists.", imgName);
            }
        }

        return img;
    }

    private org.opencv.core.Point matchingMethod(String fileName) {
        Mat result = new Mat();
        int result_cols = img.cols() - templ.cols() + 1;
        int result_rows = img.rows() - templ.rows() + 1;
        result.create(result_rows, result_cols, CvType.CV_8UC1);
        if (use_mask && method_accepts_mask) {
            Imgproc.matchTemplate(img, templ, result, match_method, mask);
        } else {
            Imgproc.matchTemplate(img, templ, result, match_method);
        }
        //Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());
        org.opencv.core.Point matchLoc;
        int val = 0;
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
        if (method_accepts_mask) {
            matchLoc = mmr.minLoc;
            val = (int) mmr.minVal;
            logger.log(Level.FINE, "{0} - Probability: {1} {2} {3}", new String[] {fileName, String.valueOf(val/2000), String.valueOf(round(matchLoc.x)), String.valueOf(round(matchLoc.y))});
            if (val/2000 <= 700) {
                return matchLoc;
            }
        } else {
            matchLoc = mmr.maxLoc;
            val = (int) mmr.maxVal;
            logger.log(Level.FINE, "{0} - Probability: {1} {2} {3}", new String[] {fileName, String.valueOf(val), String.valueOf(round(matchLoc.x)), String.valueOf(round(matchLoc.y))});
            if (val >= 4) {
                return matchLoc;
            }
        }
        return null;
    }

    static class RGB {
        final int precise = 50;
        int r;
        int g;
        int b;

        public RGB(int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RGB rgb = (RGB) o;
            return r == rgb.r &&
                    g == rgb.g &&
                    b == rgb.b;
        }

        public boolean isSimilar(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RGB rgb = (RGB) o;
            return beetween(precise*-1, precise, r - rgb.r)
                    && beetween(precise*-1, precise, g - rgb.g)
                    && beetween(precise*-1, precise, b - rgb.b);
        }

        private boolean beetween(int left, int right, int value) {
            return value >= left && value <= right;
        }


        @Override
        public int hashCode() {
            return Objects.hash(r, g, b);
        }

        @Override
        public String toString() {
            return r + " " + g + " " + b;
        }
    }

    static class XYV {
        final int precise = 3;
        final int value;
        RGB rgb;
        final int x;
        final int y;

        public XYV(int x, int y, int value) {
            this.x = x;
            this.y = y;
            this.value = value;
        }

        public XYV(int x, int y, int value, RGB rgb) {
            this(x, y, value);
            this.rgb = rgb;
        }

        public boolean isSimilar(XYV firstValueRight) {
            return beetween(precise*-1, precise, this.rgb.r - firstValueRight.rgb.r)
                    && beetween(precise*-1, precise, this.rgb.g - firstValueRight.rgb.g)
                    && beetween(precise*-1, precise, this.rgb.b - firstValueRight.rgb.b);
        }

        private boolean beetween(int left, int right, int value) {
            return value >= left && value <= right;
        }

    }
}
