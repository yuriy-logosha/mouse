package com.home.mouse.server.processors;

import com.home.mouse.server.controller.MouseController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.home.mouse.server.processors.CommandProcessor.Commands.*;
import static com.home.mouse.server.processors.CommandProcessor.Response.*;
import static java.lang.Math.round;

public class CommandProcessor {

    private static final Logger logger = Logger.getLogger(CommandProcessor.class.getName());
    private final ImageProcessor processor;
    private MouseController mouseController;
    private Map<String, BufferedImage> cache = new HashMap();

    public CommandProcessor(MouseController mouseController, ImageProcessor processor) {
        this.mouseController = mouseController;
        this.processor = processor;
    }

    public String process(String command) {
        String[] commandLine = command.split(" ");
        String commandName = commandLine[0];
        commandLine = Arrays.copyOfRange(commandLine, 1, commandLine.length);
        if (command != null) {
            try {
                Response result = execute(commandName, commandLine);
                return result.getResponse();
            } catch (AWTException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }
        return "";
    }

    private Response execute(String command, String[] line) throws AWTException, IOException {
        Robot robot = mouseController.getRobot();
        if (EXIT.getValue().equalsIgnoreCase(command)) {
            mouseController.exit();
            return new Response("Exiting...");

        } else if (MOVE.getValue().equalsIgnoreCase(command)) {
            int x = Integer.valueOf(line[0]);
            int y = Integer.valueOf(line[1]);
            robot.mouseMove(x, y);
            return buildResponse(MOVED, new Long[]{Long.valueOf(x), Long.valueOf(y)});

        } else if (CLEAN.getValue().equalsIgnoreCase(command)) {
            info("Removing {0} cached files.", new Object[] {cache.size()});
            cache.clear();

        } else if (REFRESH.getValue().equalsIgnoreCase(command)) {
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
            String fileName = line.length >0?line[0]:"screen.png";
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage capture = new Robot().createScreenCapture(screenRect);
            File file = new File(fileName);
            ImageIO.write(capture, "png", file);
            info("Captured to file: {0}", new String[]{file.getPath()});

        } else if ("screenRange2File".equalsIgnoreCase(command)) {
            int x = Integer.valueOf(line[1]);
            int y = Integer.valueOf(line[2]);
            int x2 = Integer.valueOf(line[3]);
            int y2 = Integer.valueOf(line[4]);
            Rectangle screenRect = new Rectangle(x, y, x2 - x, y2 - y);
            BufferedImage capture = robot.createScreenCapture(screenRect);
            ImageIO.write(capture, "png", new File(line[0]));
            info("Captured to file: {0}", new Object[] {line[0]});


        } else if ("contains".equalsIgnoreCase(command) || "containsInScreen".equalsIgnoreCase(command)
                || "containsEx".equalsIgnoreCase(command) || "containsInScreenEx".equalsIgnoreCase(command)) {
            BufferedImage rightImage = getImage(line[0]);
            Point point = command.endsWith("Ex")?
                    processor.containsEx(getScreenCapture(), rightImage):
                    processor.contains(getScreenCapture(), rightImage);
            if (point != null) {
                return buildResponse(FOUND, new Long[] {round(point.getX()), round(point.getY())});
            } else {
                return buildResponse(NOT_FOUND);
            }
        } else if (CONTAINS_ALL.getValue().equalsIgnoreCase(command)) {

            Point point = processor
                    .contains(getScreenCapture(), Arrays
                        .stream(line)
                            .filter(pic -> !pic.isEmpty())
                            .map(pic -> getImage(pic))
                            .toArray(BufferedImage[]::new));
            if (point != null) {
                return buildResponse(FOUND, new Long[] {round(point.getX()), round(point.getY())});
            } else {
                return buildResponse(NOT_FOUND);
            }
        } else if (CONTAINS_ALL_IN_RANGE.getValue().equalsIgnoreCase(command)) {
            int beginX = Integer.valueOf(line[0]);
            int beginY = Integer.valueOf(line[1]);
            int finishX = Integer.valueOf(line[2]);
            int finishY = Integer.valueOf(line[3]);
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

            if (finishX < beginX) {
                finishX = screenSize.height;
            }
            if (finishY < beginY) {
                finishY = screenSize.width;
            }

            Point point = processor
                    .contains(getScreenCapture(new Rectangle(beginX, beginY, finishY - beginY, finishX - beginX)), Arrays
                            .stream(line)
                            .filter(pic -> !pic.isEmpty())
                            .map(pic -> getImage(pic))
                            .toArray(BufferedImage[]::new));
            if (point != null) {
                return buildResponse(FOUND, new Long[] {round(point.getX()), round(point.getY())});
            } else {
                return buildResponse(NOT_FOUND);
            }
        } else if (CONTAINS_IN_RANGE.getValue().equalsIgnoreCase(command)) {
            int beginX = Integer.valueOf(line[1]);
            int beginY = Integer.valueOf(line[2]);
            int finishX = Integer.valueOf(line[3]);
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

            if (finishX < beginX) {
                finishX = screenSize.height;
            }
            int finishY = Integer.valueOf(line[4]);
            if (finishY < beginY) {
                finishY = screenSize.width;
            }

            Point point = processor.contains(getScreenCapture(new Rectangle(beginX, beginY, finishX - beginX, finishY - beginY)), getImage(line[0]));
            if(point != null) {
                return buildResponse(FOUND, new Long[] {round(point.getX()), round(point.getY())});
            } else {
                return buildResponse(NOT_FOUND);
            }

        } else if (SHOW.getValue().equalsIgnoreCase(command)) {
            long x = round(MouseInfo.getPointerInfo().getLocation().getX());
            long y = round(MouseInfo.getPointerInfo().getLocation().getY());
            String result = x + " " + y;
            return buildResponse(result);

        } else if ("getcolor".equalsIgnoreCase(command)) {
            try {
                int x = Integer.valueOf(line[0]);
                int y = Integer.valueOf(line[1]);

                Color pixelColor = robot.getPixelColor(x, y);

                String result = pixelColor.getRed() + " " + pixelColor.getGreen() + " " + pixelColor.getBlue();
                return buildResponse(result);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }  else if ("print".equalsIgnoreCase(command)) {
            processor.printImage(getImage(line[0]));
        }
        return buildResponse(EMPTY);
    }

    private BufferedImage getImage(String imgName) {
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

    public Response buildResponse(String value, Long[]... args) {
        if(value.equals(EMPTY))
            return new Response("");
        else if(value.equals(FOUND)) {
            info(FOUND, args);
            return new Response(args[0] + " " + args[1]);
        } else if(value.equals(MOVED)) {
            info(MOVED, args);
            return new Response(args[0] + " " + args[1]);
        } else {
            info(value);
            return new Response(value);
        }
    }

    class Response {
        public static final String EMPTY = "";
        public static final String NOT_FOUND = "Not found";
        public static final String FOUND = "Found {0} {1}";
        public static final String MOVED = "Moved to {0}:{1}";

        public String getResponse() {
            return response;
        }

        private String response;

        public Response(String r) {
            response = r;
        }
    }

    enum Commands {
        EXIT("exit"),
        MOVE("move"),
        CLEAN("clean"),
        REFRESH("refresh"),
        SHOW("show"),
        CONTAINS_IN_RANGE("containsInRange"),
        CONTAINS_ALL("containsAll"),
        CONTAINS_ALL_IN_RANGE("containsAllInRange");

        private String name;

        Commands(String name) {
            this.name = name;
        }

        public String getValue() {
            return this.name;
        }
    }

}
