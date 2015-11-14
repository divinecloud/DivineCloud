
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

package com.dc.util.logscanner;

public class MessageWrapper {
    private String timestamp;
    private ErrorStack errorStack;

    public MessageWrapper(String timestamp, ErrorStack errorStack) {
        this.timestamp = timestamp;
        this.errorStack = errorStack;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public ErrorStack getErrorStack() {
        return errorStack;
    }
}
