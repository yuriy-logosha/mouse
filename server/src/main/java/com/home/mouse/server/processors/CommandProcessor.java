package com.home.mouse.server.processors;

import com.home.mouse.server.controller.MouseController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class CommandProcessor {

    private MouseController mouseController;

    public CommandProcessor(MouseController mouseController) {
        this.mouseController = mouseController;
    }

    public String process(String command) throws AWTException, IOException {
        String[] commandLine = command.split(" ");
        String commandName = commandLine[0];
        commandLine = Arrays.copyOfRange(commandLine, 1, commandLine.length);
        if (command != null) {
            return execute(commandName, commandLine);
        }
        return "";
    }

    private String execute(String command, String[] line) throws AWTException, IOException {
        Robot robot = mouseController.getRobot();
        if ("exit".equalsIgnoreCase(command)) {
            mouseController.exit();
            System.out.println("Exiting...");
            return "Exiting...";
        } else if ("move".equalsIgnoreCase(command)) {
            int x = Integer.valueOf(line[0]);
            int y = Integer.valueOf(line[1]);
            robot.mouseMove(x, y);
            System.out.println("Move to " + x + ":" + y);
            return "Moved to " + x + ":" + y;
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
            //ImageIO.write(screenCapture, "png", new File("screen.png"));
            Point point = ImageProcessor.contains(screenCapture, ImageIO.read(new File(line[0])));
            if(point != null) {
                System.out.println("Found: " + Math.round(point.getX()) + " " + Math.round(point.getY()));
                return Math.round(point.getX()) + " " + Math.round(point.getY());
            } else {
                System.out.println("Not found");
                return "Not found";
            }
        } else if ("containsEx".equalsIgnoreCase(command) || "containsInScreenEx".equalsIgnoreCase(command)) {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            BufferedImage screenCapture = robot.createScreenCapture(new Rectangle(screenSize));
            Point point = ImageProcessor.containsEx(screenCapture, ImageIO.read(new File(line[0])));
            if(point != null) {
                System.out.println("Found: " + Math.round(point.getX()) + " " + Math.round(point.getY()));
                return Math.round(point.getX()) + " " + Math.round(point.getY());
            } else {
                System.out.println("Not found");
                return "Not found";
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

            Point point = ImageProcessor.contains(screenCapture, ImageIO.read(new File(line[0])));
            if(point != null) {
                long roundX = Math.round(point.getX() + x);
                long roundY = Math.round(point.getY() + y);
                System.out.println("Found: " + roundX + " " + roundY);
                return roundX + " " + roundY;
            } else {
                System.out.println("Not found");
                return "Not found";
            }

        } else if ("refresh".equalsIgnoreCase(command)) {
            robot = new Robot();

        } else if ("show".equalsIgnoreCase(command)) {
            System.out.println(Math.round(MouseInfo.getPointerInfo().getLocation().getX()) + " " + Math.round(MouseInfo.getPointerInfo().getLocation().getY()));
            return Math.round(MouseInfo.getPointerInfo().getLocation().getX()) + " " + Math.round(MouseInfo.getPointerInfo().getLocation().getY());
        } else if ("getcolor".equalsIgnoreCase(command)) {
            try {
                int x = Integer.valueOf(line[0]);
                int y = Integer.valueOf(line[1]);

                Color pixelColor = robot.getPixelColor(x, y);

                String result = pixelColor.getRed() + " " + pixelColor.getGreen() + " " + pixelColor.getBlue();
                System.out.println(result);
                return result;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

}
