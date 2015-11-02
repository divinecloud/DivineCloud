/* *******************************************************************************
 *  Copyright 2011 Divine Cloud Inc.  All Rights Reserved.                       *
 *  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.                *
 *                                                                               *
 *  This code is property of Divine Cloud software (www.divinecloud.com),        *
 *  and cannot be used without valid license purchase.                           *
 *  Any part of code cannot be modified or distributed to others without the     *
 *  written permission from Divine Cloud.                                        *
 *                                                                               *
 *  This code is provided in the hope that it will benefit the user, but         *
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY   *
 *  or FITNESS FOR A PARTICULAR PURPOSE. Divine Cloud is not liable for any      *
 *  bugs in the software that can cause potential loss (monetarily or otherwise) *
 *  to the user.                                                                 *
 *                                                                               *
 *  Please contact Divine Cloud if you need additional information or have any   *
 *  questions.                                                                   *
 *********************************************************************************/

package com.dc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;

/**
 * DT Logger Factory for retrieving the logger instances, to be used by all
 * sources within the DT System.
 */
public class DcLoggerFactory {
    private Logger                   logger;
    private Logger                   consoleLogger;
    private volatile boolean         initialized;
    private static DcLoggerFactory instance = new DcLoggerFactory();

    private DcLoggerFactory() {
    }

    public static DcLoggerFactory getInstance() {
        return instance;
    }

    private synchronized void initialize() {
        if (!initialized) {
            logger = LoggerFactory.getLogger("dt");
            consoleLogger = LoggerFactory.getLogger("dtConsole");
            initialized = true;
        }
    }

    public Logger getLogger() {
        if (!initialized) {
            initialize();
        }
        return logger;
    }

    public Logger getConsoleLogger() {
        if (!initialized) {
            initialize();
        }
        return consoleLogger;
    }

    public void changeToDebugLevel() {
        ((ch.qos.logback.classic.Logger) logger).setLevel(Level.DEBUG);
        ((ch.qos.logback.classic.Logger) consoleLogger).setLevel(Level.DEBUG);
    }

    public void changeToInfoLevel() {
        ((ch.qos.logback.classic.Logger) logger).setLevel(Level.INFO);
        ((ch.qos.logback.classic.Logger) consoleLogger).setLevel(Level.INFO);
    }

    public void changeToWarnLevel() {
        ((ch.qos.logback.classic.Logger) logger).setLevel(Level.WARN);
        ((ch.qos.logback.classic.Logger) consoleLogger).setLevel(Level.WARN);
    }

    public void changeToErrorLevel() {
        ((ch.qos.logback.classic.Logger) logger).setLevel(Level.ERROR);
        ((ch.qos.logback.classic.Logger) consoleLogger).setLevel(Level.ERROR);
    }

    public void changeConsoleToDebugLevel() {
        ((ch.qos.logback.classic.Logger) logger).setLevel(Level.DEBUG);
        ((ch.qos.logback.classic.Logger) consoleLogger).setLevel(Level.DEBUG);
    }

    public void changeConsoleToInfoLevel() {
        ((ch.qos.logback.classic.Logger) logger).setLevel(Level.INFO);
        ((ch.qos.logback.classic.Logger) consoleLogger).setLevel(Level.INFO);
    }

    public void changeConsoleToWarnLevel() {
        ((ch.qos.logback.classic.Logger) logger).setLevel(Level.WARN);
        ((ch.qos.logback.classic.Logger) consoleLogger).setLevel(Level.WARN);
    }

    public void changeConsoleToErrorLevel() {
        ((ch.qos.logback.classic.Logger) logger).setLevel(Level.ERROR);
        ((ch.qos.logback.classic.Logger) consoleLogger).setLevel(Level.ERROR);
    }

    public boolean turnOffConsoleLogger() {
        boolean result = false;
        // Only one of the two logger should be allowed to be turned off not
        // both.
        if (Level.OFF != ((ch.qos.logback.classic.Logger) logger).getLevel()) {
            ((ch.qos.logback.classic.Logger) consoleLogger).setLevel(Level.OFF);
            result = true;
        }
        return result;
    }

    public boolean turnOffLogger() {
        boolean result = false;
        // Only one of the two logger should be allowed to be turned off not
        // both.
        if (Level.OFF != ((ch.qos.logback.classic.Logger) consoleLogger).getLevel()) {
            ((ch.qos.logback.classic.Logger) logger).setLevel(Level.OFF);
            result = true;
        }
        return result;
    }
}
