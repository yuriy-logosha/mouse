package com.home.mouse.server.processors.model;

import com.home.mouse.server.processors.CommandProcessor;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Response {

    private static final Logger logger = Logger.getLogger(CommandProcessor.class.getName());

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

    public static Response buildResponse(String value, String... args) {
        if(value.equals(FOUND)) {
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

    public static Response buildResponse(String value) {
        if(value.equals(EMPTY)) {
            return new Response("");
        } else {
            info(value);
            return new Response(value);
        }
    }

    public static void info(String msg) {
        logger.log(Level.INFO, msg);
    }

    public static void info(String msg, Object[] objs) {
        logger.log(Level.INFO, msg, objs);
    }
}
