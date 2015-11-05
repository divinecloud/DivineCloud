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

package com.dc.runbook.rt.exec.support;

public class ColorCodeFilter {

    public static String filter(String text) {
        if(text != null) {
            text = text.replaceAll("\\[\\d*;\\d*m", ""); //Removes any color codes from the output.
            text = text.replaceAll("\\[\\d*m", ""); //Removes any color codes from the output.
        }
        return text;
    }
}
