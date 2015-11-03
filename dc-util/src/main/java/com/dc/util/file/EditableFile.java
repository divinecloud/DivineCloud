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

/*
 * Copyright (C) 2014 Divine Cloud Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dc.util.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.dc.util.string.EnhancedStringBuilder;

/**
 * Allows users to modify the contents of the file. After the file is modified use the save method to persist the changes.
 * Until the save method is called the changes are cached in the memory.
 *
 * Since all the file contents are loaded into memory, this class is only useful for small to medium sized text files.
 * For very large text files this class should NOT be used, else it can cause Out Of Memory errors.
 */
public class EditableFile {
    private File                  file;
    private EnhancedStringBuilder cache;

    public EditableFile(File file) throws IOException {
        this.file = file;
        if (file == null || !file.isFile()) {
            throw new FileNotFoundException("File " + file + " Not Found");
        }
        load();
    }

    private void load() throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            StringBuilder strBuffer = new StringBuilder((int) file.length());
            char buffer[] = new char[(int) file.length()];
            reader.read(buffer, 0, (int) file.length());
            strBuffer.append(buffer);
            cache = new EnhancedStringBuilder(strBuffer);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    public void saveAndClose() throws IOException {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(cache.toString().toCharArray(), 0, cache.length());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public void insertBefore(String insertBefore, String text) throws IllegalArgumentException {
        cache.insertBefore(insertBefore, text);
    }

    public void insertAfter(String insertAfter, String text) throws IllegalArgumentException {
        cache.insertAfter(insertAfter, text);
    }

    public void delete(String text) throws IllegalArgumentException {
        cache.delete(text);
    }

    public void replace(String replacee, String replacer) throws IllegalArgumentException {
        cache.replace(replacee, replacer);
    }

    public void replaceAll(String replacee, String replacer) throws IllegalArgumentException {
        cache.replaceAll(replacee, replacer);
    }

    public boolean exists(String text) {
        return cache.exists(text);
    }

    public void append(String text) {
        cache.append(text);
    }

    public void print() {
        System.out.println(cache.toString());
    }

    // Sample usage of the class
    public static void main(String[] args) {
        File file = new File("/opt/sample/report.txt");
        try {
            EditableFile reportFile = new EditableFile(file);
            reportFile.insertBefore("foo", "bar");
            reportFile.delete("foo");
            reportFile.replace("bar", "foo");
            reportFile.replaceAll("bar", "foo");
            reportFile.saveAndClose();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
