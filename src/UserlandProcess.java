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
            semaphore.acquire(); // thread.state == WAITING
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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
     * acquires (decrements) the semaphore, stopping this thread from running
     */
    void stop() {
        try {
            semaphore.acquire(semaphore.availablePermits());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

   /**
    * switches process when {@code quantumExpired} == true
    */
    void cooperate() {
        if(quantumExpired) {
           OS.switchProcess();
           quantumExpired = false;
        }
    }

   /**
    * sets quantumExpired, indicating that this process’ quantum has expired
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
