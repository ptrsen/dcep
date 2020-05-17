package com.dcep.main;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class dcep {

    private static final Logger consolelogger = LogManager.getLogger("console_log");

    public static void main(String[] args) {

        consolelogger.trace("Trace Message!");
        consolelogger.debug("Debug Message!");
        consolelogger.info("Info Message!");
        consolelogger.warn("Warn Message!");
        consolelogger.error("Error Message Logged !!!", new NullPointerException("NullError"));
        consolelogger.fatal("Fatal Message!");



    }
}
