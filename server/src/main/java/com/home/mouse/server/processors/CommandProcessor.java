package com.home.mouse.server.processors;

import com.home.mouse.server.controller.MouseController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Math.round;

public class CommandProcessor {

    private static final Logger logger = Logger.getLogger(CommandProcessor.class.getName());
    private static final String NOT_FOUND = "Not found";
    private MouseController mouseController;
    private Map<String, BufferedImage> cache = new HashMap();

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
            info("Exiting...");
            return "Exiting...";

        } else if ("move".equalsIgnoreCase(command)) {
            int x = Integer.valueOf(line[0]);
            int y = Integer.valueOf(line[1]);
            robot.mouseMove(x, y);
            String result = "Moved to " + x + ":" + y;
            info(result);
            return result;

        } else if ("clean".equalsIgnoreCase(command)) {
            info("Removing {0} cached files.", new Object[] {cache.size()});
            cache.clear();

        } else if ("refresh".equalsIgnoreCase(command)) {
            robot = new Robot();

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
            info("Captured to file: {0}; Size: {1}x{2}; Resolution: {3}",
                    new Object[]{line[0],
                            Toolkit.getDefaultToolkit().getScreenSize().getHeight(),
                            Toolkit.getDefaultToolkit().getScreenSize().getWidth(),
                            Toolkit.getDefaultToolkit().getScreenResolution()});

        } else if ("screenRange2File".equalsIgnoreCase(command)) {
            int x = Integer.valueOf(line[1]);
            int y = Integer.valueOf(line[2]);
            int x2 = Integer.valueOf(line[3]);
            int y2 = Integer.valueOf(line[4]);
            Rectangle screenRect = new Rectangle(x, y, x2 - x, y2 - y);
            BufferedImage capture = robot.createScreenCapture(screenRect);
            ImageIO.write(capture, "png", new File(line[0]));
            info("Captured to file: {0}; Size: {1}x{2}",
                    new Object[] {line[0],
                            capture.getHeight(),
                            capture.getWidth()});


        } else if ("contains".equalsIgnoreCase(command) || "containsInScreen".equalsIgnoreCase(command)
                || "containsEx".equalsIgnoreCase(command) || "containsInScreenEx".equalsIgnoreCase(command)) {
            BufferedImage img = getImage(line[0]);
            Point point = command.endsWith("Ex")?
                            ImageProcessor.containsEx(getScreenCapture(), img):
                            ImageProcessor.contains(getScreenCapture(), img);
            if (point != null) {
                long x = round(point.getX());
                long y = round(point.getY());
                info("Found: {0} {1}", new Long[] {x, y});
                return x + " " + y;
            } else {
                info(NOT_FOUND);
                return NOT_FOUND;
            }
        } else if ("containsInRange".equalsIgnoreCase(command)) {
            int beginX = Integer.valueOf(line[1]);
            int beginY = Integer.valueOf(line[2]);
            int finishX = Integer.valueOf(line[3]);
            int finishY = Integer.valueOf(line[4]);

            Point point = ImageProcessor.contains(getScreenCapture(new Rectangle(beginX, beginY, finishX, finishY)), getImage(line[0]));
            if(point != null) {
                long roundX = round(point.getX() + beginX);
                long roundY = round(point.getY() + beginY);
                info("Found: {0} {1}", new Long[] {roundX, roundY});
                return roundX + " " + roundY;
            } else {
                info(NOT_FOUND);
                return NOT_FOUND;
            }

        } else if ("show".equalsIgnoreCase(command)) {
            long x = round(MouseInfo.getPointerInfo().getLocation().getX());
            long y = round(MouseInfo.getPointerInfo().getLocation().getY());
            String result = x + " " + y;
            info(result);
            return result;

        } else if ("getcolor".equalsIgnoreCase(command)) {
            try {
                int x = Integer.valueOf(line[0]);
                int y = Integer.valueOf(line[1]);

                Color pixelColor = robot.getPixelColor(x, y);

                String result = pixelColor.getRed() + " " + pixelColor.getGreen() + " " + pixelColor.getBlue();
                info(result);
                return result;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }  else if ("print".equalsIgnoreCase(command)) {
            ImageProcessor.printImage(getImage(line[0]));
        }
        return "";
    }

    private BufferedImage getImage(String imgName) throws IOException {
        BufferedImage img = cache.get(imgName);

        if (img == null) {
            img = ImageIO.read(new File(imgName));
            cache.put(imgName, img);
        }

        return img;
    }

    private BufferedImage getScreenCapture() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        return getScreenCapture(new Rectangle(screenSize));
    }

    private BufferedImage getScreenCapture(Rectangle rectangle) {
        Robot robot = mouseController.getRobot();
        return robot.createScreenCapture(rectangle);
    }

    private void info(String msg) {
        logger.log(Level.INFO, msg);
    }

    private void info(String msg, Object[] objs) {
        logger.log(Level.INFO, msg, objs);
    }

}
