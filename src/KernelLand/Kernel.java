package KernelLand;

import Devices.Device;
import Devices.VFS;
import UserLand.UserlandProcess;
import Utility.OSPrinter;

/* Shared memory */
import static KernelLand.OS.currentCall;
import static KernelLand.OS.returnValue;
import static KernelLand.OS.params;

import java.util.concurrent.Semaphore;

public class Kernel implements Runnable, Device {
    /** Runs our kernel execution */
    private static final Thread thread = new Thread(new Kernel(), "KernelLand.Kernel");

    static int cores; // Amount of cores in our computer
    /** Indicates when our {@code thread} is allowed to run */
    private static Semaphore semaphore;

    static Scheduler scheduler;
    static VFS vfs;

    Kernel() {
        if(thread != null){
            cores = 1;
            semaphore = new Semaphore(cores);
            semaphore.drainPermits();

            scheduler = new Scheduler();
            vfs = new VFS();

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
            /* Lock Thread */
            try {
                synchronized (OS.getLock()){ OS.notifyComplete(); }
                semaphore.acquire(); // KernelLand.Kernel.thread.state == WAITING
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            OSPrinter.print("Kernel: Running " + OS.currentCall + " -> ");

            /* Get call and run it*/
            switch (OS.currentCall) {
                /* Scheduler */
                case CREATEPROCESS -> returnValue = scheduler.createProcess
                        ((UserlandProcess) params.get(0), (OS.Priority) params.get(1));
                case SWITCHPROCESS -> scheduler.switchProcess();
                case SLEEP -> scheduler.sleep((Integer) params.get(0));
                case SENDMESSAGE    -> scheduler.sendMessage((Message)params.get(0));
                case WAITFORMESSAGE -> returnValue = scheduler.waitForMessage();

                /* Vfs */
                case OPEN  -> returnValue = vfs.open((String) params.get(0));
                case CLOSE -> vfs.close((int) params.get(0));
                case READ  -> returnValue = vfs.read((int)params.get(0), (int)params.get(1));
                case SEEK  -> vfs.seek((int)params.get(0), (int)params.get(1));
                case WRITE -> vfs.write((int)params.get(0), (byte[]) params.get(1));

                case SHUTDOWN -> { return; }
            }
            scheduler.runningPCB.run();
        }
    }

    @Override
    public int open(String s) {
        int[] processIDs = scheduler.getRunningPCB().getDeviceIDs();
        int vfsID, count;

        /* Find empty position */
        count = 0;
        while(processIDs[count] != -1) {
            if(++count >= processIDs.length)
                return -1; // no empty spot
        }
        vfsID = vfs.open(Integer.toString(count));

        if(vfsID == -1) return -1;
        return processIDs[count] = vfsID;
    }

    @Override
    public void close(int id) {
        int[] processIDs = scheduler.getRunningPCB().getDeviceIDs();

        vfs.close(processIDs[id]);
        processIDs[id] = -1;
    }

    @Override
    public byte[] read(int id, int size) {
        int[] processIDs = scheduler.getRunningPCB().getDeviceIDs();
        
        return vfs.read(processIDs[id], size);
    }

    @Override
    public void seek(int id, int to) {
        int[] processIDs = scheduler.getRunningPCB().getDeviceIDs();

        vfs.seek(processIDs[id], to);
    }

    @Override
    public int write(int id, byte[] data) {
        int[] processIDs = scheduler.getRunningPCB().getDeviceIDs();

        return vfs.write(processIDs[id], data);
    }
}
