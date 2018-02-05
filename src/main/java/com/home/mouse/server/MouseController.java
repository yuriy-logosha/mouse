package com.home.mouse.server;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MouseController {
    private Robot robot;
    boolean isExit = false;
    DataInputStream in;
    DataOutputStream out;

    public MouseController(Robot robot) throws AWTException {
        this.robot = robot;
    }

    public void start() {
        try {
            ServerSocket ss = new ServerSocket(6666); // создаем сокет сервера и привязываем его к вышеуказанному порту
            System.out.println("Waiting for a command...");

            while (!isExit) {
                Socket socket = ss.accept(); // заставляем сервер ждать подключений и выводим сообщение когда кто-то связался с сервером

                InputStream sin = socket.getInputStream();
                OutputStream sout = socket.getOutputStream();

                in = new DataInputStream(sin);
                out = new DataOutputStream(sout);

                String line = null;

                try {
                    line = in.readUTF();

                    System.out.println(line);

                    if (line.contains(";")) {
                        String[] commands = line.split(";");
                        for (String command : commands) {
                            process(command.trim());
                        }
                    } else {
                        process(line);
                    }
                    out.writeUTF("Done");
                    out.flush();

                } catch (IOException | NumberFormatException | AWTException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException x) {
            System.out.println("Can not create listener on port 6666");
            x.printStackTrace();
        }

    }

    private void process(String command) throws AWTException, IOException {
        String[] commandLine = command.split(" ");
        String commandName = commandLine[0];
        commandLine = Arrays.copyOfRange(commandLine, 1, commandLine.length);
        if (command != null) {
            execute(commandName, commandLine);
        }
    }

    private void execute(String command, String[] line) throws AWTException, IOException {
        if ("exit".equalsIgnoreCase(command)) {
            isExit = true;
        } else if ("move".equalsIgnoreCase(command)) {
            int x = Integer.valueOf(line[0]);
            int y = Integer.valueOf(line[1]);
            robot.mouseMove(x, y);
        } else if ("lclick".equalsIgnoreCase(command)) {
            robot.mousePress(InputEvent.BUTTON1_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_MASK);
        } else if ("mclick".equalsIgnoreCase(command)) {
            robot.mousePress(InputEvent.BUTTON2_MASK);
            robot.mouseRelease(InputEvent.BUTTON2_MASK);
        } else if ("rclick".equalsIgnoreCase(command)) {
            robot.mousePress(InputEvent.BUTTON3_MASK);
            robot.mouseRelease(InputEvent.BUTTON3_MASK);
        } else if ("dblclick".equalsIgnoreCase(command)) {
            robot.mousePress(InputEvent.BUTTON1_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_MASK);
            robot.delay(50);
            robot.mousePress(InputEvent.BUTTON1_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_MASK);
        } else if ("presskey".equalsIgnoreCase(command)) {
            for (String s : line) {
                robot.keyPress(Integer.parseInt(s));
                robot.keyRelease(Integer.parseInt(s));
            }
        } else if ("sleep".equalsIgnoreCase(command)) {
            robot.delay(Integer.valueOf(line[0]));

        } else if ("screen2File".equalsIgnoreCase(command)) {
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage capture = new Robot().createScreenCapture(screenRect);
            ImageIO.write(capture, "png", new File(line[0]));
            System.out.println("Captured to file: " + line[0]);
            System.out.println("Screen size: " + Toolkit.getDefaultToolkit().getScreenSize());
            System.out.println("Screen resolution: " + Toolkit.getDefaultToolkit().getScreenResolution());

        } else if ("screenRange2File".equalsIgnoreCase(command)) {

            int x = 0;
            try {
                x = Integer.valueOf(line[1]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            int y = Integer.valueOf(line[2]);
            int x2 = Integer.valueOf(line[3]);
            int y2 = Integer.valueOf(line[4]);
            Rectangle screenRect = new Rectangle(x, y, x2 - x, y2 - y);
            BufferedImage capture = new Robot().createScreenCapture(screenRect);
            ImageIO.write(capture, "png", new File(line[0]));
            System.out.println("File name: " + line[0]);
            System.out.println("Picture size: " + capture.getHeight() + "x" + capture.getWidth());
        } else if ("contains".equalsIgnoreCase(command) || "containsInScreen".equalsIgnoreCase(command)) {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            BufferedImage screenCapture = robot.createScreenCapture(new Rectangle(screenSize));
            ImageIO.write(screenCapture, "png", new File("screen.png"));
            Point point = contains(screenCapture, ImageIO.read(new File(line[0])));
            if(point != null) {
                System.out.println("Found: " + Math.round(point.getX()) + " " + Math.round(point.getY()));
                out.writeUTF( Math.round(point.getX()) + " " + Math.round(point.getY()));
            } else {
                System.out.println("Not found");
                out.writeUTF( "Not found");
            }
        } else if ("containsInRange".equalsIgnoreCase(command)) {
            int x = 0;
            try {
                x = Integer.valueOf(line[1]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            int y = Integer.valueOf(line[2]);
            int x2 = Integer.valueOf(line[3]);
            int y2 = Integer.valueOf(line[4]);

            BufferedImage screenCapture = robot.createScreenCapture(
                    new Rectangle(x, y, x2, y2));

            Point point = contains(screenCapture, ImageIO.read(new File(line[0])));
            if(point != null) {
                long roundX = Math.round(point.getX() + x);
                long roundY = Math.round(point.getY() + y);
                System.out.println("Found: " + roundX + " " + roundY);
                out.writeUTF( roundX + " " + roundY);
            } else {
                System.out.println("Not found");
                out.writeUTF( "Not found");
            }

        } else if ("refresh".equalsIgnoreCase(command)) {
            robot = new Robot();

        } else if ("show".equalsIgnoreCase(command)) {
            System.out.println(Math.round(MouseInfo.getPointerInfo().getLocation().getX()) + " " + Math.round(MouseInfo.getPointerInfo().getLocation().getY()));
            out.writeUTF(Math.round(MouseInfo.getPointerInfo().getLocation().getX()) + " " + Math.round(MouseInfo.getPointerInfo().getLocation().getY()));
        } else if ("getcolor".equalsIgnoreCase(command)) {
            try {
                int x = Integer.valueOf(line[0]);
                int y = Integer.valueOf(line[1]);

                Color pixelColor = robot.getPixelColor(x, y);

                String result = pixelColor.getRed() + " " + pixelColor.getGreen() + " " + pixelColor.getBlue();
                System.out.println(result);
                out.writeUTF(result);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    private Point contains(BufferedImage bigImage, BufferedImage subImage) {
        for (int hY = 0; hY <= bigImage.getHeight() - subImage.getHeight(); hY++) {
            for (int hX = 0; hX <= bigImage.getWidth() - subImage.getWidth(); hX++) {
                int halfX = subImage.getWidth() / 2;
                int halfY = subImage.getHeight() / 2;
                int deviation = Math.abs(bigImage.getRGB(hX, hY)) - Math.abs(subImage.getRGB(0, 0));
                newCheck:
                if (deviation <= 3 && deviation >= -3) {
                    for (int nY = 0; nY < subImage.getHeight(); nY++) {
                        for (int nX = 0; nX < subImage.getWidth(); nX++) {
                            int deviation2 = Math.abs(bigImage.getRGB(hX + nX, hY + nY)) - Math.abs(subImage.getRGB(nX, nY));
                            if (!(deviation2 >=-1 && deviation2 <= 1)) {
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

    private void printImage(BufferedImage image) {
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
