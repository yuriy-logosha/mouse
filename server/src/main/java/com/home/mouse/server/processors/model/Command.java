package com.home.mouse.server.processors.model;

import java.util.Arrays;

public class Command {
    public final Commands commandName;
    public final String[] arguments;
    boolean coordXLoaded = false;
    boolean coordYLoaded = false;
    boolean coordX1Loaded = false;
    boolean coordY1Loaded = false;
    int x;
    int y;
    int x1;
    int y1;

    public Command(Commands cmd, String[] args) {
        commandName = cmd;
        this.arguments = args;
    }

    public static Command buildFrom(String commandLine) {
        String[] arguments = commandLine.split(" ");
        return new Command(Commands.valueOf(arguments[0].toUpperCase()), Arrays.copyOfRange(arguments, 1, arguments.length));
    }

    public int getDelay() {
        if(!coordXLoaded) {
            x = Integer.valueOf(arguments[0]);
            coordXLoaded = true;
        }
        return x;
    }

    public int getX() {
        if(!coordXLoaded) {
            x = Integer.valueOf(arguments[0]);
            coordXLoaded = true;
        }
        return x;
    }

    public int getY() {
        if(!coordYLoaded) {
            y = Integer.valueOf(arguments[1]);
            coordYLoaded = true;
        }
        return y;
    }

    public int getX1() {
        if(!coordX1Loaded) {
            x = Integer.valueOf(arguments[2]);
            coordX1Loaded = true;
        }
        return x;
    }

    public int getY1() {
        if(!coordY1Loaded) {
            y = Integer.valueOf(arguments[3]);
            coordY1Loaded = true;
        }
        return y;
    }

    public String getRaw0() {
        return arguments[0];
    }

    public String getRaw1() {
        return arguments[1];
    }

}
