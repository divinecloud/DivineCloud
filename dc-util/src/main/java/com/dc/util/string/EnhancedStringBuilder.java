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

package com.dc.util.string;

/**
 * This Class is a enhanced version of StringBuilder class provided by java API.
 *
 * It provides the following additional functionality:
 *
 * 1) search for a string
 *
 * 2) Insert a string after or before another String.
 *
 * 3) Replace a string with a new string
 *
 * 4) Delete a string.
 */
public class EnhancedStringBuilder {

    private StringBuilder cache;

    public EnhancedStringBuilder(StringBuilder buffer) throws IllegalArgumentException {
        if (buffer == null) {
            throw new IllegalArgumentException("Null value passed for the StringBuilder");
        }
        cache = buffer;
    }

    public boolean exists(String text) {
        boolean found = false;
        int index = cache.indexOf(text);
        if (index >= 0) {
            found = true;
        }
        return found;
    }

    public int search(String text) {
        return cache.indexOf(text);
    }

    public void insertBefore(String insertBefore, String text) {
        int index = search(insertBefore);
        if (index >= 0) {
            cache.insert(index, text);
        }
    }

    public void insertAfter(String insertAfter, String text) {
        int index = search(insertAfter);
        if (index >= 0) {
            cache.insert(index + insertAfter.length(), text);
        }
    }

    public int delete(String text) throws IllegalArgumentException {
        int index = search(text);
        if (index >= 0) {
            cache.delete(index, index + text.length());
        }
        return index;
    }

    public void replace(String replacee, String replacer) {
        int index = delete(replacee);
        if (index >= 0) {
            cache.insert(index, replacer);
        }
    }

    public void replaceAll(String replacee, String replacer) {
        if (replacee != null && replacer != null && !replacer.contains(replacee)) {
            int index = 0;
            int length = replacee.length();
            while ((index = cache.indexOf(replacee)) > -1) {
                cache.delete(index, index + length);
                cache.insert(index, replacer);
            }
        }
    }

    public void append(String text) {
        int index = length();
        if (index >= 0) {
            cache.insert(index, text);
        }
    }

    public int length() {
        return cache.length();
    }

    public String toString() {
        return cache.toString();
    }

    public static void main(String args[]) {
        String replacer = "ec2-user ";
        String replacee = "ec2-user";
        String input = "slfdkj ec2-user sdoijsodfijsdfa dovijsdfovijfd";
        StringBuilder sb = new StringBuilder(input);
        EnhancedStringBuilder esb = new EnhancedStringBuilder(sb);

        esb.replaceAll(replacee, replacer);
        System.out.println("Done");
    }
}
