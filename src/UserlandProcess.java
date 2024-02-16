import java.util.concurrent.Semaphore;

public abstract class UserlandProcess implements Runnable {
    private final Thread thread;
    Semaphore semaphore;
    int pid; //TODO this

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
        /* Wait for thread to become avalabe */
        while(thread.getState() == Thread.State.NEW) {Thread.onSpinWait();}
    }

    abstract void main();

    @Override
    public void run()
    {
        try {
            semaphore.acquire(); // wait until os is ready
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        start(); // so main can run
        main();
    }

    /**
     * releases (increments) the semaphore, allowing this thread to run
     */
    void start()
    {
       semaphore.release();
    }

    /**
     * TODO Stops thread from running main process, switches processes and waits to be started again
     * how do you make the running thread call stop unless it calls cooperate
     */
    void stop() {
        semaphore.drainPermits();
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        semaphore.release();
    }

   /**
    * switches process when quantum is expired and waits to be scheduled
    */
     boolean cooperate() {
        if(quantumExpired) {
           OS.switchProcess(); // calls stop to process
           quantumExpired = false;

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
    void waitForInterrupt() {
        while(!cooperate())
            Thread.onSpinWait();
    }

   /**
    * Sets quantumExpired, indicating that this processes quantum has expired.
    * Stops when thread hits cooperate
    */
   void requestStop()
   {
       quantumExpired = true;
   }


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
