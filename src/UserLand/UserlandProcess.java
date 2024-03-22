package UserLand;

import KernelLand.OS;
import java.util.concurrent.Semaphore;

public abstract class UserlandProcess implements Runnable {
    final Thread thread;
    UserlandProcess parentThread;
    private final Semaphore semaphore;
    public int pid;

    boolean quantumExpired;
    int timeoutCounter = 0;

    OS.Priority priority;


    /**
     * Constructor for Userland process, initializes process with one thread
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

    private void switchProcess() {
        OS.switchProcess();
    }

    /** Wait until quantum expired to continue */
    void cooperateOnInterrupt() {
        while(!cooperate())
            Thread.onSpinWait();
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
   public void requestStop() { quantumExpired = true; }


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

