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

package com.dc.ssh.batch.sftp;

import com.dc.ssh.client.sftp.SftpClientException;
import com.dc.support.KeyValuePair;

import java.util.List;

public interface SftpBatchCallback {

    public String executionId();

    public void completeForNode(String displayId);

    public void completeForNode(String displayId, SftpClientException cause);

    public void done();

    public void failed(List<KeyValuePair<String, String>> failedNodesList);

    public void cancelled();

    public boolean isDone();

    public void percentageCompleteForNode(String displayId, int percent);

    public SftpClientException getCause();
}
