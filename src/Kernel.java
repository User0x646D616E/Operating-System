import java.util.concurrent.Semaphore;

public class Kernel implements Runnable {

    public static Thread thread = new Thread(new Kernel(), "Kernel");
    /** Indicates when our {@code thread} is allowed to run */
    static Semaphore semaphore;

    static Scheduler scheduler;

    static int cores; // Amount of cores in our computer


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
                semaphore.acquire(); // Kernel.thread.state == WAITING
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            OSPrinter.print("Kernel: Running " + OS.currentCall + " -> ");

            switch (OS.currentCall) {
                case CREATEPROCESS -> scheduler.createProcess((UserlandProcess) OS.params.get(0));
                case SWITCHPROCESS -> scheduler.switchProcess();
                case SHUTDOWN -> { return; }
            }
            scheduler.currProcess.start();
        }
    }
}
