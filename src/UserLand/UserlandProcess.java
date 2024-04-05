package UserLand;

import KernelLand.OS;
import Utility.OSPrinter;

import java.util.Arrays;
import java.util.concurrent.Semaphore;

import static KernelLand.Kernel.PAGE_COUNT;
import static KernelLand.Kernel.PAGE_SIZE;
import static KernelLand.PCB.PROCESS_PAGE_COUNT;

public abstract class UserlandProcess implements Runnable {

    /* THREAD EXECUTION */
    final Thread thread;
    UserlandProcess parentThread;
    private final Semaphore semaphore;

    /* SCHEDULING */
    boolean quantumExpired;
    int timeoutCounter = 0;
    OS.Priority priority;
    public int pid;

    /* MEMORY */
    /** The memory available to our Machine */
    private static final byte[] memory = new byte[PAGE_SIZE * PAGE_COUNT];

    /** Translation look aside buffer caches frequently used virtual to physical memory mappings.
     * maps 2 virtual pages to their respective physical pages.
     * Column 0 holds virtual addresses and column 1 holds the physical map */
    public static int[][] tlb = new int[2][2];


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
     * Reads a byte from memory.
     * Converts a virtual address to a physical address and reads from memory
     *
     * @param address the virtual address to be read from
     * @return data read from address
     */
    public byte read(int address) {
        if(address > PAGE_SIZE * PROCESS_PAGE_COUNT || address < 0)
            throw new RuntimeException("Attempted memory access out of bounds");

        final int virtualPage, pageOffset, physicalAddress;

        virtualPage = address / PAGE_SIZE; // virtual page to physical page mapping
        pageOffset = address % PAGE_SIZE;

        /* check if address is in tlb */
        for(int[] virtual_physical : tlb)
        {
            if(virtualPage == virtual_physical[0]) { // column 0 being virtual page
                /* calculate and return byte at physical address */
                physicalAddress = virtual_physical[1] + pageOffset;
                return memory[physicalAddress];
            }
        }

        /* Get physical address mapping */
        int tlbRow = OS.getMapping(virtualPage);

        physicalAddress = tlb[tlbRow][1]*PAGE_SIZE + pageOffset;
        if(physicalAddress == -1) { // no mapping exists
            OSPrinter.printf("ERROR: UserlandProcess Read: no mapping exists for virtual address %d\n", address);
            return -1;
        }

        return memory[physicalAddress];
    }

    /**
     * Writes a byte to memory
     *
     * @param address to write to
     * @param value byte to write
     */
    public int write(int address, byte value) {
        if(address > PAGE_SIZE * PROCESS_PAGE_COUNT || address < 0)
            throw new RuntimeException("Attempted memory access out of bounds");

        final int virtualPage, pageOffset, physicalAddress;

        virtualPage = address / PAGE_SIZE; // virtual page to physical page mapping
        pageOffset = address % PAGE_SIZE;

        /* check if address is in tlb */
        for (int[] virtual_physical : tlb)
        {
            if(virtualPage == virtual_physical[0]) { // column 0 being virtual page
                /* calculate and return physical address */
                physicalAddress = virtual_physical[1] + pageOffset;
                memory[physicalAddress] = value;
                return 0;
            }
        }

        /* Get physical address mapping */
        int tlbRow = OS.getMapping(virtualPage);

        physicalAddress = tlb[tlbRow][1]*PAGE_SIZE + pageOffset;
        if(physicalAddress == -1) { // no mapping exists
            OSPrinter.printf("ERROR: UserlandProcess Write: no mapping exists for virtual address %d\n", address);
            return -1;
        }

        memory[physicalAddress] = value;
        return 0;
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

    public static int[][] getTlb() {
        return tlb;
    }

    public static void clearTlb() {
        for(int [] virtual_physical : tlb)
        {
            Arrays.fill(virtual_physical, -1);
        }
    }

    public static byte[] getMemory() {
        return memory;
    }
}

