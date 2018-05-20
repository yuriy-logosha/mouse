package com.home.mouse.server.processors;

import com.home.mouse.server.controller.MouseController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Math.round;

public class CommandProcessor {

    private final static Logger logger = Logger.getLogger(CommandProcessor.class.getName());
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
            logger.log(Level.INFO, "Exiting...");
            return "Exiting...";

        } else if ("move".equalsIgnoreCase(command)) {
            int x = Integer.valueOf(line[0]);
            int y = Integer.valueOf(line[1]);
            robot.mouseMove(x, y);
            logger.log(Level.INFO, "Moved to {1}:{2}", new Object[]{x, y});
            return "Moved to " + x + ":" + y;

        } else if ("mousePress1".equalsIgnoreCase(command)) {
            robot.mousePress(InputEvent.BUTTON1_MASK);

        } else if ("mouseRelease1".equalsIgnoreCase(command)) {
            robot.mouseRelease(InputEvent.BUTTON1_MASK);

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
            int delay = Integer.valueOf(line[0]);

            robot.mousePress(InputEvent.BUTTON1_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_MASK);
            robot.delay(delay);
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
            logger.log(Level.INFO, "Captured to file: {1}; Size: {2}; Resolution: {3}",
                    new Object[]{line[0], Toolkit.getDefaultToolkit().getScreenSize(), Toolkit.getDefaultToolkit().getScreenResolution()});

        } else if ("screenRange2File".equalsIgnoreCase(command)) {
            int x = Integer.valueOf(line[1]);
            int y = Integer.valueOf(line[2]);
            int x2 = Integer.valueOf(line[3]);
            int y2 = Integer.valueOf(line[4]);
            Rectangle screenRect = new Rectangle(x, y, x2 - x, y2 - y);
            BufferedImage capture = robot.createScreenCapture(screenRect);
            ImageIO.write(capture, "png", new File(line[0]));
            System.out.println("File name: " + line[0]);
            System.out.println("Picture size: " + capture.getHeight() + "x" + capture.getWidth());

        } else if ("contains".equalsIgnoreCase(command) || "containsInScreen".equalsIgnoreCase(command)) {
            Point point = ImageProcessor.contains(getScreenCapture(), ImageIO.read(new File(line[0])));
            if(point != null) {
                System.out.println("Found: " + round(point.getX()) + " " + round(point.getY()));
                return round(point.getX()) + " " + round(point.getY());
            } else {
                System.out.println("Not found");
                return "Not found";
            }

        } else if ("containsEx".equalsIgnoreCase(command) || "containsInScreenEx".equalsIgnoreCase(command)) {
            Point point = ImageProcessor.containsEx(getScreenCapture(), ImageIO.read(new File(line[0])));
            if(point != null) {
                System.out.println("Found: " + round(point.getX()) + " " + round(point.getY()));
                return round(point.getX()) + " " + round(point.getY());
            } else {
                System.out.println("Not found");
                return "Not found";
            }

        } else if ("containsInRange".equalsIgnoreCase(command)) {
            int beginX = Integer.valueOf(line[1]);
            int beginY = Integer.valueOf(line[2]);
            int finishX = Integer.valueOf(line[3]);
            int finishY = Integer.valueOf(line[4]);

            Point point = ImageProcessor.contains(getScreenCapture(new Rectangle(beginX, beginY, finishX, finishY)), ImageIO.read(new File(line[0])));
            if(point != null) {
                long roundX = round(point.getX() + beginX);
                long roundY = round(point.getY() + beginY);
                System.out.println("Found: " + roundX + " " + roundY);
                return roundX + " " + roundY;
            } else {
                System.out.println("Not found");
                return "Not found";
            }

        } else if ("refresh".equalsIgnoreCase(command)) {
            robot = new Robot();

        } else if ("show".equalsIgnoreCase(command)) {
            System.out.println(round(MouseInfo.getPointerInfo().getLocation().getX()) + " " + round(MouseInfo.getPointerInfo().getLocation().getY()));
            return round(MouseInfo.getPointerInfo().getLocation().getX()) + " " + round(MouseInfo.getPointerInfo().getLocation().getY());

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

    private BufferedImage getScreenCapture() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        return getScreenCapture(new Rectangle(screenSize));
    }

    private BufferedImage getScreenCapture(Rectangle rectangle) {
        Robot robot = mouseController.getRobot();
        return robot.createScreenCapture(rectangle);
    }

}
