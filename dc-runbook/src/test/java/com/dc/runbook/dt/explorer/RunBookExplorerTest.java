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

package com.dc.runbook.dt.explorer;

import com.dc.runbook.TestSupport;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class RunBookExplorerTest {

    @Test
    public void testRunBooksCount() {
        String resourceFolderPath = TestSupport.getProperty("resources.path");
        String runBookFolderPath = resourceFolderPath + "/data/runbooks";

        String sample1RunBooksPath = runBookFolderPath + "/sample1";
        String sample2RunBooksPath = runBookFolderPath + "/sample2";
        long startTime = System.nanoTime();
        int count = RunBookExplorer.count(new File(sample1RunBooksPath));
        int count2 = RunBookExplorer.count(new File(sample2RunBooksPath));
        long endTime = System.nanoTime();
        System.out.println("Time : " + (endTime - startTime));
        assertEquals(4, count);
        assertEquals(10, count2);
    }

    @Test
    public void testRunBookListCount() {
        String resourceFolderPath = TestSupport.getProperty("resources.path");
        String runBookFolderPath = resourceFolderPath + "/data/runbooks";

        String sample1RunBooksPath = runBookFolderPath + "/sample1";
        String sample2RunBooksPath = runBookFolderPath + "/sample2";

        List<File> list = new ArrayList<>();
        list.add(new File(sample1RunBooksPath));
        list.add(new File(sample2RunBooksPath));
        long startTime = System.nanoTime();
        int count = RunBookExplorer.count(list);
        long endTime = System.nanoTime();
        System.out.println("Time : " + (endTime - startTime));
        assertEquals(14, count);

    }
}
