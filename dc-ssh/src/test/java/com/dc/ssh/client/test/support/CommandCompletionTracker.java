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

package com.dc.ssh.client.test.support;


import com.dc.ssh.client.exec.SshClient;

public class CommandCompletionTracker extends Thread {

    private SampleCallback callback;
    private SshClient sshClient;
    private long cancelAfter;
    private volatile boolean cancel;
    private String executionId;

    public CommandCompletionTracker(SampleCallback callback) {
        this.callback = callback;
    }

    public CommandCompletionTracker(SampleCallback callback, SshClient sshClient, long cancelAfter, String executionId) {
        this.callback = callback;
        this.sshClient = sshClient;
        this.cancelAfter = cancelAfter;
        this.executionId = executionId;
        cancel = true;
    }

    public void run() {
        long passedTime = 0;
        if(callback != null) {
            while(!callback.isDone()) {
                try {
                    Thread.sleep(25);
                    passedTime += 25;
                    if(cancel) {
                        if(passedTime >= cancelAfter) {
                            sshClient.cancel(executionId);
                            break;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Command execution complete");
        }
    }
}
