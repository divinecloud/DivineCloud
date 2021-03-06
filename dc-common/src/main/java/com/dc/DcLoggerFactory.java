/*
 *
 *  * Copyright (C) 2014 Divine Cloud Inc.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

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
