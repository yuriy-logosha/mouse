package com.home.mouse.server.proc;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageProcessor {

    public static Point contains(BufferedImage bigImage, BufferedImage subImage) {
        for (int hY = 0; hY < bigImage.getHeight() - subImage.getHeight(); hY++) {
            for (int hX = 0; hX < bigImage.getWidth() - subImage.getWidth(); hX++) {
                int deviation = bigImage.getRGB(hX, hY) - subImage.getRGB(0, 0);
                newCheck:
                if (deviation <= 3 && deviation >= -3) {
                    for (int nY = 0; nY < subImage.getHeight(); nY++) {
                        for (int nX = 0; nX < subImage.getWidth(); nX++) {
                            int deviation2 = bigImage.getRGB(hX + nX, hY + nY) - subImage.getRGB(nX, nY);
                            if (!(deviation2 >= -1 && deviation2 <= 1)) {
                                break newCheck;
                            }
                        }
                    }
                    return new Point(hX, hY);
                }
            }
        }
        return null;
    }

    public static Point containsEx(BufferedImage bigImage, BufferedImage subImage) {

        int subImageWidth = subImage.getWidth();
        int subImageHeight = subImage.getHeight();

        int halfWidth = subImageWidth / 2;
        int halhHeight = subImageHeight / 2;
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

}
