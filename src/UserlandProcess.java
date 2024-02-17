import java.util.concurrent.Semaphore;

public abstract class UserlandProcess implements Runnable {
    private final Thread thread;
    Semaphore semaphore;
    int pid;

    boolean quantumExpired;


    /**
     * Constructor for Userland process, initializes process with one thread
     */
    public UserlandProcess(){
        thread = new Thread(this, "" + getClass());

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
        try {
            semaphore.acquire(); // wait until OS is ready and start is called
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        main();
    }

   /**
    * Halts process if quantum expired or has no available permits
    */
     boolean cooperate() {
        if(quantumExpired) {
           OS.switchProcess();
           quantumExpired = false;

            /* Stop process */
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return true;
        }
        return false;
    }

    /** Wait until quantum expired to continue */
    void cooperateOnInterrupt() {
        while(!cooperate())
            Thread.onSpinWait();
    }

    /**
     * releases (increments) the semaphore, allowing this thread to run
     */
    void start() { semaphore.release(); }

    /**
     * Stops thread from running main process, switches processes and waits to be started again
     *
     */
    void stop() {
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
   void requestStop() { quantumExpired = true; }


   /**
    * indicates if the semaphore is 0
    * @return true if semaphore is 0, false otherwise
    */
   boolean isStopped()
   {
       return semaphore.availablePermits() == 0;
   }

   /**
    * @return true when the java thread is not alive
    */
   boolean isDone()
   {
       return !thread.isAlive();
   }

}
