package com.home.mouse;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static java.lang.Math.round;

public class ImageProcessor {
    private Robot robot;

    public ImageProcessor() throws AWTException {
        robot = new Robot();
    }

    public static void main(String[] line) {
        String command = line[0];

        try {
            ImageProcessor ip = new ImageProcessor();
            ip.executeCmd(command, line);
        } catch (AWTException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void executeCmd(String command, String[] line) throws IOException {
        if ("contains".equalsIgnoreCase(command) || "containsInScreen".equalsIgnoreCase(command)) {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            BufferedImage screenCapture = robot.createScreenCapture(new Rectangle(screenSize));
            Point point = contains(screenCapture, ImageIO.read(new File(line[1])));
            if(point != null) {
                System.out.println(round(point.getX()) + " " + round(point.getY()));
            } else {
                System.out.println("Not found");
            }

        } else if ("containsEx".equalsIgnoreCase(command) || "containsInScreenEx".equalsIgnoreCase(command)) {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            BufferedImage screenCapture = robot.createScreenCapture(new Rectangle(screenSize));
            Point point = containsEx(screenCapture, ImageIO.read(new File(line[1])));
            if(point != null) {
                System.out.println(round(point.getX()) + " " + round(point.getY()));
            } else {
                System.out.println("Not found");
            }

        } else if ("containsInRange".equalsIgnoreCase(command)) {
            int beginX = Integer.valueOf(line[2]);
            int beginY = Integer.valueOf(line[3]);
            int finishX = Integer.valueOf(line[4]);
            int finishY = Integer.valueOf(line[5]);

            BufferedImage screenCapture = robot.createScreenCapture(new Rectangle(beginX, beginY, finishX, finishY));

            Point point = contains(screenCapture, ImageIO.read(new File(line[1])));
            if (point != null) {
                long roundX = round(point.getX() + beginX);
                long roundY = round(point.getY() + beginY);
                System.out.println(roundX + " " + roundY);
            } else {
                System.out.println("Not found");
            }
        }
    }

    public static Point contains(BufferedImage leftImage, BufferedImage rightImage) {
        XYV firstNotZeroedRightValue = getFirstNotZeroedValue(rightImage);
        for (int hX = 0; hX < leftImage.getWidth(); hX++) {
            for (int hY = 0; hY < leftImage.getHeight(); hY++) {

                XYV leftValue = getXYV(leftImage, hX, hY);

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
        for (int nX = xyv.x; nX < rightImage.getWidth(); nX++) {
            for (int nY = xyv.y; nY < rightImage.getHeight() && rightImage.getRGB(nX, nY) != 0; nY++) {
                if(leftImage.getWidth() -1 < nX + shiftX || leftImage.getHeight() -1 < nY + shiftY) {
                    return false;
                }
                XYV leftValue = getXYV(leftImage,nX + shiftX, nY + shiftY);
                XYV rightValue = getXYV(rightImage, nX, nY);
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
        for (int x = 0; x < rightImage.getWidth(); x++) {
            for (int y = 0; y < rightImage.getHeight() && rightImage.getRGB(y, x) != 0; y++) {
                return getXYV(rightImage, y, x);
            }
        }
        return null;
    }

    private static XYV getXYV(BufferedImage img, int x, int y) {
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

    static class RGB {
        int r;
        int g;
        int b;

        public RGB(int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }
    }

    static class XYV {
        int x;
        int y;
        int value;
        RGB rgb;

        public XYV(int x, int y, int value, RGB rgb) {
            this.x = x;
            this.y = y;
            this.value = value;
            this.rgb = rgb;
        }
    }

    private static boolean beetween(int left, int right, int value) {
        return value >= left && value <= right;
    }

    public static Point containsEx(BufferedImage bigImage, BufferedImage subImage) {

        int subImageWidth = subImage.getWidth();
        int subImageHeight = subImage.getHeight();

        int halfWidth = subImageWidth / 2;
        int halhHeight = subImageHeight / 2;

/*
        XY[] dot = new XY[5];

        dot[0] = new XY(0, 0);
        dot[1] = new XY(subImageWidth - 1, subImageHeight - 1);
        dot[2] = new XY(halfWidth, halhHeight);
        dot[3] = new XY(0, subImageHeight - 1);
        dot[4] = new XY(subImageWidth - 1, 0);
*/

        int[] keyPointsX = new int[]{0, subImageWidth - 1, halfWidth, 0, subImageWidth - 1};
        int[] keyPointsY = new int[]{0, subImageHeight - 1, halhHeight, subImageHeight - 1, 0};
        int[] keyPointsValues = new int[]{
                subImage.getRGB(keyPointsX[0], keyPointsY[0]),
                subImage.getRGB(keyPointsX[1], keyPointsY[1]),
                subImage.getRGB(keyPointsX[2], keyPointsY[2]),
                subImage.getRGB(keyPointsX[3], keyPointsY[3]),
                subImage.getRGB(keyPointsX[4], keyPointsY[4])};

        for (int hY = 0; hY < bigImage.getHeight() - subImageHeight; hY++) {
            for (int hX = 0; hX < bigImage.getWidth() - subImageWidth; hX++) {
                if (bigImage.getRGB(hX + keyPointsX[0], hY + keyPointsY[0]) == keyPointsValues[0] &&
                        bigImage.getRGB(hX + keyPointsX[1], hY + keyPointsY[1]) == keyPointsValues[1] &&
                        bigImage.getRGB(hX + keyPointsX[2], hY + keyPointsY[2]) == keyPointsValues[2] &&
                        bigImage.getRGB(hX + keyPointsX[3], hY + keyPointsY[3]) == keyPointsValues[3] &&
                        bigImage.getRGB(hX + keyPointsX[4], hY + keyPointsY[4]) == keyPointsValues[4] &&
                        checkAll(bigImage, subImage, hX, hY))
                    return new Point(hX, hY);
            }
        }
        return null;
    }

    static class XY {
        final int x;
        final int y;

        public XY(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private static boolean checkAll(BufferedImage bigImage, BufferedImage subImage, int x1, int y1) {
        for (int nY = 0; nY < subImage.getHeight(); nY++) {
            for (int nX = 0; nX < subImage.getWidth(); nX++) {
                //int deviation = bigImage.getRGB(x1 + nX, y1 + nY) - subImage.getRGB(nX, nY);
                if (bigImage.getRGB(x1 + nX, y1 + nY) != subImage.getRGB(nX, nY)) {
                    return false;
                }
            }
        }

        return true;
    }

    public static void printImageRGB(BufferedImage image) {
        System.out.print("____");
        for (int hX = 0; hX < ((image.getWidth() >= 150) ? 150 : image.getWidth()); hX++) {
            System.out.print(("         " + hX + " ").substring(String.valueOf(hX).length()));
        }
        System.out.println("|");
        for (int hY = 0; hY < ((image.getHeight() >= 150) ? 150 : image.getHeight()); hY++) {
            System.out.print(("000" + hY).substring(String.valueOf(hY).length()) + "|");
            for (int hX = 0; hX < ((image.getWidth() >= 150) ? 150 : image.getWidth()); hX++) {
                Object dataElements = image.getRaster().getDataElements(hX, hY, null);
                String value1 = image.getColorModel().getRed(dataElements) + "";
                String value2 = image.getColorModel().getGreen(dataElements) + "";
                String value3 = image.getColorModel().getBlue(dataElements) + "";
                System.out.print(("000" + value1).substring(value1.length()) + ("000" + value2).substring(value2.length()) + ("000" + value3).substring(value3.length()) + " ");
            }
            System.out.println("|");
        }
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

}
