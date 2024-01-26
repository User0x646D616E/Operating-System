import java.util.concurrent.Semaphore;

public abstract class UserlandProcess implements Runnable {
   private Thread thread;
   private Semaphore semaphore;

   boolean quantumExpired;


   UserlandProcess(){
      thread = new Thread("user thread");
      semaphore = new Semaphore(1);
   }

   /**
    * sets quantumExpired, indicating that this process’ quantum has expired
    */
   void requestStop()
   {
      quantumExpired = true;
   }

   abstract void main();

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

   /**
    * releases (increments) the semaphore, allowing this thread to run
    */
   void start()
   {
      semaphore.release();
   }

   /**
    * acquires (decrements the semaphore, stopping this thread from running
    */
   void stop() throws InterruptedException {
      semaphore.acquire(semaphore.availablePermits());
   }

   /**
    * acquire the semaphore, then call main
    */
   public void run()
   {
      try {
         semaphore.acquire();
      } catch (InterruptedException e) {
         throw new RuntimeException(e);
      }
      main();
   }

   /**
    * switches process when {@code quantumExpired} == true
    */
   void cooperate()
   {
      if(quantumExpired){
         quantumExpired = false;
         OS.switchProcess();
      }
   }
}
