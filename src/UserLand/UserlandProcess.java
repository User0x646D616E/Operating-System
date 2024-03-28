package UserLand;

import KernelLand.OS;
import java.util.concurrent.Semaphore;

public abstract class UserlandProcess implements Runnable {

    /* THREAD EXECUTION */
    final Thread thread;
    UserlandProcess parentThread;
    private final Semaphore semaphore;
    public int pid;


    /* SCHEDULING */
    boolean quantumExpired;
    int timeoutCounter = 0;
    OS.Priority priority;


    /* MEMORY */
    final int PAGE_SIZE = 1024;
    /** The memory available to our OS */
    static byte[] memory = new byte[1024*1024];

    /** Translation look aside buffer caches frequently used virtual to physical memory mappings.
     * maps 2 virtual addresses to their respective physical address.
     * Column 0 holds virtual addresses and column 1 holds the physical map */
    static int[][] tlb = new int[2][2];


    /**
     * Constructor for UserLand process, initializes process with one thread
     */
    public UserlandProcess(){
        thread = new Thread(this, getClass().getSimpleName());

        semaphore = new Semaphore(1);
        semaphore.drainPermits();
        quantumExpired = false;

        thread.start();

        /* Wait for thread to become available */
        while(thread.getState() == Thread.State.NEW) {Thread.onSpinWait();}
    }

    /**
     * Reads data from virtual memory
     * Converts a virtual address to a physical address and reads from memory
     *
     * @param address the virtual address to be read from
     * @return the physical address
     */
    public byte read(int address) {
        // search tlb for virtual address
        for(int i = 0; i < tlb[0].length; i++) { // column 0 being virtual mappings
            if(address == tlb[0][i])
                return memory[calcPhysicalAddress(address)]; // calculate and return physical address
        }

        OS.getMapping(address); // look in memory
        return 0;
    }

    /** Writes data from virtual memory */
    public void write(int address, byte value) {
    }

    /** takes a virtual address and calculates the physical address */
    private int calcPhysicalAddress(int virtual) {
        final int virtualPage, pageOffset, physical; // constant folding
        virtualPage = virtual / PAGE_SIZE;
        pageOffset  = virtual % PAGE_SIZE;
        physical = virtualPage * PAGE_SIZE + pageOffset;

        return physical;
    }

    abstract void main();

    @Override
    public void run()
    {
        parentThread = this;
        try {
            semaphore.acquire(); // wait until KernelLand.OS is ready and start is called
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        main();
    }

   /**
    * Halts process if quantum expired or has no available permits
    */
     boolean cooperate() {
         if(!quantumExpired)
             return false;

         timeoutCounter++;
         parentThread.switchProcess();
         quantumExpired = false;
         stop();

         return true;
    }

    /** Wait until quantum expired to continue */
    void cooperateOnInterrupt() {
        while(!cooperate())
            Thread.onSpinWait();
    }

    private void switchProcess() {
        OS.switchProcess();
    }

    /**
     * Releases (increments) the semaphore, allowing this thread to run
     */
    public void start() { semaphore.release(); }

    /**
     * Stops thread from running main process, switches processes and waits to be started again
     *
     */
    public void stop() {
        semaphore.drainPermits();
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

   /**
    * Sets quantumExpired, indicating that this processes quantum has expired.
    * Waits for thread to hit cooperate to stop
    */
   public void requestStop() {
       quantumExpired = true;
   }



   /**
    * indicates if the semaphore is 0
    * @return true if semaphore is 0, false otherwise
    */
   public boolean isStopped()
   {
       return semaphore.availablePermits() == 0;
   }

   /**
    * @return true when the java thread is not alive
    */
   public boolean isDone()
   {
       return !thread.isAlive();
   }

   public void setPriority(OS.Priority priority){
       this.priority = priority;
   }

    public int getTimeoutCounter() {
        return timeoutCounter;
    }

    public void setTimeoutCounter(int timeoutCounter) {
        this.timeoutCounter = timeoutCounter;
    }

    public Thread getThread() {
       return thread;
    }
}

