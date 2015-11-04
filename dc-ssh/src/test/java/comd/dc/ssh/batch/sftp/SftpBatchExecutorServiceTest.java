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

package comd.dc.ssh.batch.sftp;

import com.dc.ssh.batch.sftp.SftpBatchExecutorService;
import com.dc.ssh.batch.sftp.TaskCompleteNotification;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

public class SftpBatchExecutorServiceTest {
    private volatile boolean done;

    @Test
    public void testServiceBlockingWhenFull() {
        SftpBatchExecutorService service = new SftpBatchExecutorService("sample_exec", 10, new NotificationReceiver(20), true);
        for(int i=0; i<20; i++) {
            SampleTask task = new SampleTask(i+1);
            service.submit(task);
            System.out.println("Submitted for id : " + (i + 1));
        }
        while(!done) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    class NotificationReceiver implements TaskCompleteNotification {
        private int total;
        private AtomicInteger count;

        public NotificationReceiver(int total) {
            this.total = total;
            count = new AtomicInteger();
        }

        @Override
        public void taskComplete(String id) {
            int val = count.incrementAndGet();
            System.out.println("id = " + id);
            if(val == total) {
                done = true;
            }
        }
    }
}

class SampleTask implements Callable {
    private int id;

    public SampleTask(int id) {
        this.id = id;
    }

    @Override
    public Object call() throws Exception {
        System.out.println("id : " + id + " - START - " + System.currentTimeMillis());
        Thread.sleep(10000);
        System.out.println("id : " + id + " - END - " + System.currentTimeMillis());
        return null;
    }
}

