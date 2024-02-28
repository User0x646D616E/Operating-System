package KernelLand;

import Devices.VFS;
import UserLand.UserlandProcess;

import java.util.concurrent.Semaphore;

public class Kernel implements Runnable {
    /** Runs our kernel execution */
    private static final Thread thread = new Thread(new Kernel(), "KernelLand.Kernel");

    /** Indicates when our {@code thread} is allowed to run */
    int cores; // Amount of cores in our computer
    private static Semaphore semaphore;

    static Scheduler scheduler;
    static VFS vfs;

    Kernel(){
        if(thread != null){
            cores = 1;
            semaphore = new Semaphore(cores);
            semaphore.drainPermits();
            scheduler = new Scheduler();

            thread.setPriority(1);
            thread.start();
        }
    }

    public void start()
    {
        semaphore.release();
    }

    @Override
    public void run() {
        while(true)
        {
            try {
                synchronized (OS.getLock()){ OS.notifyComplete(); }
                semaphore.acquire(); // KernelLand.Kernel.thread.state == WAITING
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            OSPrinter.print("KernelLand.Kernel: Running " + OS.currentCall + " -> ");

            switch (OS.currentCall) {
                case CREATEPROCESS -> OS.returnValue = scheduler.createProcess((UserlandProcess) OS.params.get(0), (OS.Priority) OS.params.get(1));
                case SWITCHPROCESS -> scheduler.switchProcess();
                case SLEEP         -> scheduler.sleep((Integer) OS.params.get(0));
                case OPEN          -> OS.returnValue = vfs.open((String) OS.params.get(0));
//                case CLOSE         -> vfs.close();
//                case READ          -> vfs.read();
//                case SEEK          -> vfs.seek();
//                case WRITE         -> vfs.write();
                case SHUTDOWN      -> { return; }
            }
            scheduler.runningPCB.run();
        }
    }
}
