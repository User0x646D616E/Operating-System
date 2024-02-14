import java.util.concurrent.Semaphore;

public class Kernel implements Runnable {
    /** Runs our kernel execution */
    private static final Thread thread = new Thread(new Kernel(), "Kernel");

    /** Indicates when our {@code thread} is allowed to run */
    int cores; // Amount of cores in our computer
    private static Semaphore semaphore;

    static Scheduler scheduler;

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
                case CREATEPROCESS -> {
                    OS.returnValue = scheduler.createProcess((UserlandProcess) OS.params.get(0));
                }
                case SWITCHPROCESS -> scheduler.switchProcess();
                case SLEEP -> scheduler.sleep((Integer) OS.params.get(0));
                case SHUTDOWN -> { return; }
            }
            scheduler.runningPCB.run();
        }
    }
}
