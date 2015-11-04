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

