package com.home.mouse.server.processors;

import com.home.mouse.server.TemplatesCache;
import com.home.mouse.server.controller.MouseController;
import com.home.mouse.server.processors.model.Command;
import com.home.mouse.server.processors.model.Response;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

import static com.home.mouse.server.processors.model.Response.*;
import static java.lang.Math.round;

public class CommandProcessor {

    private static final Logger logger = Logger.getLogger(CommandProcessor.class.getName());
    private final ImageProcessor processor;
    private Robot robot;
    private final MouseController mouseController;

    public CommandProcessor(MouseController mouseController, ImageProcessor processor) {
        this.mouseController = mouseController;
        this.robot = mouseController.getRobot();
        this.processor = processor;
    }

    public String process(String commandLine) {
        if (commandLine != null) {
            try {
                Response result = execute(Command.buildFrom(commandLine));
                return result.getResponse();
            } catch (AWTException | IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }
        return "";
    }

    private Response execute(Command cmd) throws AWTException, IOException {
        final int x, y;
        org.opencv.core.Point point = null;
        switch (cmd.commandName) {
            case EXIT:
                mouseController.exit();
                return new Response("Exiting...");

            case MOVE:
                robot.mouseMove(cmd.getX(), cmd.getY());
                return buildResponse(MOVED, cmd.getRaw0(), cmd.getRaw1());

            case CLEAN:
                info("Removing {0} cached files.", new Object[] {TemplatesCache.size()});
                TemplatesCache.clear();
                break;

            case REFRESH:
                robot = new Robot();
                break;

            case MOUSEPRESS1:
                robot.mousePress(InputEvent.BUTTON1_MASK);
                break;

            case MOUSERELEASE1:
                robot.mouseRelease(InputEvent.BUTTON1_MASK);
                break;

            case LCLICK:
                robot.mousePress(InputEvent.BUTTON1_MASK);
                robot.mouseRelease(InputEvent.BUTTON1_MASK);
                break;

            case MCLICK:
                robot.mousePress(InputEvent.BUTTON2_MASK);
                robot.mouseRelease(InputEvent.BUTTON2_MASK);
                break;

            case RCLICK:
                robot.mousePress(InputEvent.BUTTON3_MASK);
                robot.mouseRelease(InputEvent.BUTTON3_MASK);
                break;

            case DBLCLICK:
                robot.mousePress(InputEvent.BUTTON1_MASK);
                robot.mouseRelease(InputEvent.BUTTON1_MASK);
                robot.delay(cmd.getDelay());
                robot.mousePress(InputEvent.BUTTON1_MASK);
                robot.mouseRelease(InputEvent.BUTTON1_MASK);
                break;

            case PRESSKEY: pressKeys(robot, cmd.arguments); break;

            case SLEEP: robot.delay(cmd.getDelay()); break;

            case SCREEN2FILE:
                store2File(robot, buildFile(cmd.arguments), robot.createScreenCapture(buildRectangle()));
                break;

            case SCREENRANGE2FILE:
                store2File(robot, buildFile(cmd.arguments), robot.createScreenCapture(buildRectangle(cmd)));
                break;

            case CONTAINSOLD:
                point = processor.contains(getScreenCapture(), ImageProcessor.getImage(cmd.getRaw0()));
                if (point != null) {
                    return buildResponse(FOUND, String.valueOf((int)point.x), String.valueOf((int)point.y));
                }
                return buildResponse(NOT_FOUND);
            case CONTAINSALLOLD:
                point = processor.contains(getScreenCapture(), Arrays
                        .stream(cmd.arguments)
                        .filter(pic -> !pic.isEmpty())
                        .map(pic -> ImageProcessor.getImage(pic))
                        .toArray(BufferedImage[]::new));
                if (point != null) {
                    return buildResponse(FOUND, String.valueOf((int)point.x), String.valueOf((int)point.y));
                }
                return buildResponse(NOT_FOUND);
            case CONTAINS:
            case CONTAINSINSCREEN:
            case CONTAINSEX:
            case CONTAINSINSCREENEX:
                point = processor.containsEx(getScreenCapture(), cmd.getRaw0());
                if (point != null) {
                    return buildResponse(FOUND, String.valueOf((int)point.x), String.valueOf((int)point.y));
                }
                return buildResponse(NOT_FOUND);

            case CONTAINSALL:
                point = processor.contains(getScreenCapture(), Arrays
                        .stream(cmd.arguments)
                        .filter(pic -> !pic.isEmpty())
                        .toArray(String[]::new));
                if (point != null) {
                    return buildResponse(FOUND, String.valueOf((int)point.x), String.valueOf((int)point.y));
                }
                return buildResponse(NOT_FOUND);

            case CONTAINSALLINRANGE:
                point = processor
                        .contains(getScreenCapture(buildRectangle(cmd)), Arrays
                                .stream(cmd.arguments)
                                .filter(pic -> !pic.isEmpty())
                                .toArray(String[]::new));
                if (point != null) {
                    return buildResponse(FOUND, String.valueOf(round(point.x)), String.valueOf(round(point.x)));
                } else {
                    return buildResponse(NOT_FOUND);
                }

            case CONTAINSINRANGE:
                point = processor.containsEx(getScreenCapture(buildRectangle(cmd)), cmd.getRaw0());
                if(point != null) {
                    return buildResponse(FOUND, String.valueOf(round(point.x)), String.valueOf(round(point.y)));
                }
                return buildResponse(NOT_FOUND);

            case SHOW:
                x = (int) MouseInfo.getPointerInfo().getLocation().getX();
                y = (int) MouseInfo.getPointerInfo().getLocation().getY();
                return buildResponse(x + " " + y);

            case GETCOLOR:
                try {
                    Color pixelColor = robot.getPixelColor(cmd.getX(), cmd.getY());

                    return buildResponse(pixelColor.getRed() + " " + pixelColor.getGreen() + " " + pixelColor.getBlue());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

        }

        return buildResponse(EMPTY);
    }

    private void pressKeys(Robot robot, String[] arguments) {
        for (String s : arguments) {
            robot.keyPress(Integer.parseInt(s));
            robot.keyRelease(Integer.parseInt(s));
        }
    }

    private static void store2File(Robot robot, File file, BufferedImage screenCapture) throws IOException {
        ImageIO.write(screenCapture, "png", file);
        Response.info("Captured to file: {0}", new String[]{file.getPath()});
    }

    private static File buildFile(String[] line) {
        String fileName = line.length >0?line[0]:"screen.png";
        return new File(fileName);
    }

    private static Rectangle buildRectangle() {
        return new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
    }

    private static Rectangle buildRectangle(Command cmd) {
        int x = cmd.getX();
        int y = cmd.getY();
        int x1 = cmd.getX1();
        int y1 = cmd.getY1();
        if (x1 < x) {
            x1 = x;
        }
        if (x1 > Toolkit.getDefaultToolkit().getScreenSize().height) {
            x1 = Toolkit.getDefaultToolkit().getScreenSize().height;
        }
        if (y1 < y) {
            y1 = y;
        }
        if (y1 > Toolkit.getDefaultToolkit().getScreenSize().width) {
            y1 = Toolkit.getDefaultToolkit().getScreenSize().width;
        }

        return new Rectangle(x, y, x1 - x, y1 - y);

    }

    private BufferedImage getScreenCapture() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        return getScreenCapture(new Rectangle(screenSize));
    }

    private BufferedImage getScreenCapture(Rectangle rectangle) {
        return robot.createScreenCapture(rectangle);
    }
}
