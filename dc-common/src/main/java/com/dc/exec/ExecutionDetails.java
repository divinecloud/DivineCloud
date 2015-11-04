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

package com.dc.exec;

import java.util.Arrays;

/**
 * Contains the ssh execution details.
 */
public class ExecutionDetails {
    private byte[] output;
    private byte[] error;
    private int statusCode;

    public ExecutionDetails(int code, byte[] output, byte[] error) {
        this.output = output;
        this.error = error;
        this.statusCode = code;
    }

    public byte[] getOutput() {
        return output;
    }

    public byte[] getError() {
        return error;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public boolean isFailed() {
        return (statusCode != 0);
    }

    @Override
    public String toString() {
        return "SshExecutionDetails{" +
                "output=" + Arrays.toString(output) +
                ", error=" + Arrays.toString(error) +
                ", statusCode=" + statusCode +
                '}';
    }
}
