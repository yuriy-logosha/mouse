package com.home.mouse.server;

public class Configuration {

    public static final String RESOURCES = System.getProperty("resources") != null ? System.getProperty("resources").trim().endsWith("/") ? System.getProperty("resources") : System.getProperty("resources") + "/" : "";

}
