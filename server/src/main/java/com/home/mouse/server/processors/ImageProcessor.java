package com.home.mouse.server.processors;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageProcessor {

    public static Point contains(BufferedImage leftImage, BufferedImage rightImage) {
        XYV xyv = getFirstNotZeroedValue(rightImage);
        for (int hX = 0; hX < leftImage.getWidth(); hX++) {
            for (int hY = 0; hY < leftImage.getHeight(); hY++) {

                int firstValueLeft = leftImage.getRGB(hX, hY);

                if (isPointInRange(firstValueLeft, xyv.value) && isPictureMatch(xyv, leftImage, rightImage, hX, hY)) {
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
                int leftValue = leftImage.getRGB(nX + shiftX, nY + shiftY);
                int rightValue = rightImage.getRGB(nX, nY);
                if (!isPointInRange(leftValue, rightValue)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isPointInRange(int firstValueLeft, int firstValueRight) {
        return beetween(-3, 3, firstValueLeft - firstValueRight);
    }

    private static XYV getFirstNotZeroedValue(BufferedImage rightImage){
        for (int x = 0; x < rightImage.getWidth(); x++) {
            for (int y = 0; y < rightImage.getHeight() && rightImage.getRGB(x, y) != 0; y++) {
                return new XYV(x, y, rightImage.getRGB(x, y));
            }
        }
        return null;
    }

    static class XYV {
        int x;
        int y;
        int value;

        public XYV(int x, int y, int value) {
            this.x = x;
            this.y = y;
            this.value = value;
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
                if (    bigImage.getRGB(hX + keyPointsX[0], hY + keyPointsY[0]) == keyPointsValues[0] &&
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
        public XY(int x, int y){
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

    public static void printImage(BufferedImage image) {
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


}
