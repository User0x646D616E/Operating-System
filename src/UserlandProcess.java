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
     */
    void stop() {
        semaphore.drainPermits();
//        thread.cooperate(); // we need the thread that's running the process to immediately cooperate
    }

   /**
    * switches process when quantum is expired and waits to be scheduled
    */
     void cooperate() {
        if(quantumExpired || semaphore.availablePermits() == 0) {
           OS.switchProcess(); // calls stop to process
           quantumExpired = false;

            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }

    /** Wait until quantum expired to continue */
    void halt() {
//        while(!cooperate()) {
//            Thread.onSpinWait();
//        }
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
