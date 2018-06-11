package com.home.mouse.server.processors;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class ImageProcessor {

    private final static Logger logger = Logger.getLogger(ImageProcessor.class.getName());

    public static Point contains(BufferedImage leftImage, BufferedImage[] images) {
        Point point = null;
        for (BufferedImage image : images) {
            point = contains(leftImage, image);
            if (point != null) {
                return point;
            }
        }
        return point;
    }

    public static Point contains(BufferedImage leftImage, BufferedImage rightImage) {
        XYV firstNotZeroedRightValue = getFirstNotZeroedValue(rightImage);
        for (int hY = 0; hY < leftImage.getHeight(); hY++) {
            for (int hX = 0; hX < leftImage.getWidth(); hX++) {

                XYV leftValue = buildXYV(leftImage, hX, hY);

                if (isPointInRange(leftValue, firstNotZeroedRightValue)
                        && isPictureMatch(firstNotZeroedRightValue, leftImage, rightImage, hX, hY)) {
                    return new Point(hX, hY);
                }
            }
        }
        return null;
    }

    private static boolean isPictureMatch(XYV xyv, BufferedImage leftImage, BufferedImage rightImage, int hX, int hY) {
        int shiftX = hX - xyv.x;
        int shiftY = hY - xyv.y;
        for (int nY = xyv.y; nY < rightImage.getHeight(); nY++) {
            for (int nX = xyv.x; nX < rightImage.getWidth() && rightImage.getRGB(nX, nY) != 0; nX++) {
                if(leftImage.getWidth() -1 < nX + shiftX || leftImage.getHeight() -1 < nY + shiftY) {
                    return false;
                }
                XYV leftValue = buildXYV(leftImage,nX + shiftX, nY + shiftY);
                XYV rightValue = buildXYV(rightImage, nX, nY);
                if (!isPointInRange(leftValue, rightValue)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isPointInRange(XYV firstValueLeft, XYV firstValueRight) {
        return beetween(-3, 3, firstValueLeft.rgb.r - firstValueRight.rgb.r)
                && beetween(-3, 3, firstValueLeft.rgb.g - firstValueRight.rgb.g)
                && beetween(-3, 3, firstValueLeft.rgb.b - firstValueRight.rgb.b);
    }

    private static XYV getFirstNotZeroedValue(BufferedImage rightImage) {
        for (int y = 0; y < rightImage.getHeight(); y++) {
            for (int x = 0; x < rightImage.getWidth() && rightImage.getRGB(y, x) != 0; x++) {
                return buildXYV(rightImage, y, x);
            }
        }
        return null;
    }

    private static XYV buildXYV(BufferedImage img, int x, int y) {
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

    private static boolean beetween(int left, int right, int value) {
        return value >= left && value <= right;
    }

    public static Point containsEx(BufferedImage bigImage, BufferedImage subImage) {

        XYV[] dots = analizeImage(subImage);

        for (int hY = 0; hY < bigImage.getHeight() - subImage.getHeight(); hY++) {
            for (int hX = 0; hX < bigImage.getWidth() - subImage.getWidth(); hX++) {
                if (checkKeyPoints(dots, bigImage, hX, hY)) {
                    return new Point(hX, hY);
                }
            }
        }
        return null;
    }

    private static XYV[] analizeImage(BufferedImage subImage){
        List<XYV> ldots = new ArrayList();

        for(int y = 0; y < subImage.getHeight(); y++) {
            for(int x = 0; x < subImage.getWidth(); x++) {
                int value = subImage.getRGB(x, y);
                if(value != 0) {
                    ldots.add(buildXYV(subImage, x, y));
                }
            }
        }

        XYV[] result = new XYV[ldots.size()];
        for (int idx = 0; idx < ldots.size(); idx++) {
            result[idx] = ldots.get(idx);
        }

        return result;
    }

    private static boolean checkKeyPoints(XYV[] dots, BufferedImage bigImage, int hX, int hY) {
        XYV xyv = buildXYV(bigImage, hX, hY);
        if(!xyv.rgb.equals(dots[0].rgb)){
            return false;
        }
        int shiftX = hX - dots[0].x;
        int shiftY = hY - dots[0].y;


        for (int i = 1; i < dots.length; i++) {
            xyv = buildXYV(bigImage, dots[i].x + shiftX, dots[i].y + shiftY);
            if(!xyv.rgb.equals(dots[i].rgb)){
                return false;
            }
        }
        return true;
    }

    private static boolean checkAll(BufferedImage bigImage, BufferedImage subImage, int x1, int y1) {
        for (int nY = 0; nY < subImage.getHeight(); nY++) {
            for (int nX = 0; nX < subImage.getWidth(); nX++) {
                int leftVal = bigImage.getRGB(x1 + nX, y1 + nY);
                int rightVal = subImage.getRGB(nX, nY);
                if (bigImage.getRGB(x1 + nX, y1 + nY) != subImage.getRGB(nX, nY)) {
                    return false;
                }
            }
        }

        return true;
    }

    public static void printImageRGB(BufferedImage image) {
        int max = 300;
        ColorModel colorModel = image.getColorModel();
        Raster raster = image.getRaster();

        System.out.println("hasAlpha: " + image.getColorModel().hasAlpha());
        System.out.println("isAlphaPremultiplied: " + image.getColorModel().isAlphaPremultiplied());
        System.out.println("getTransparency: " + colorModel.getTransparency());

        System.out.print("____");

        for (int hX = 0; hX < getMin(image.getWidth(), max); hX++) {
            System.out.print(("         " + hX + " ").substring(String.valueOf(hX).length()));
        }
        System.out.println("|");
        for (int hY = 0; hY < getMin(image.getHeight(), max); hY++) {
            System.out.print(("000" + hY).substring(String.valueOf(hY).length()) + "|");
            for (int hX = 0; hX < ((image.getWidth() >= max) ? max : image.getWidth()); hX++) {
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

    static class RGB {
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

        @Override
        public int hashCode() {
            return Objects.hash(r, g, b);
        }
    }

    static class XY {
        final int x;
        final int y;

        public XY(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    static class XYV extends XY {
        final int value;
        RGB rgb;

        public XYV(int x, int y, int value) {
            super(x, y);
            this.value = value;
        }
        public XYV(int x, int y, int value, RGB rgb) {
            this(x, y, value);
            this.rgb = rgb;
        }

    }
}
