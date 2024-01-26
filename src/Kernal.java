import java.util.concurrent.Semaphore;

public class Kernal {
    Thread thread;
    Semaphore semaphore;

    Scheduler scheduler;


    Kernal()
    {
        thread = new Thread("kernal");
        semaphore = new Semaphore(1);

        thread.start();
    }

    void start()
    {
        semaphore.release();
    }

    void run() throws InterruptedException {
        semaphore.acquire();

        switch(OS.currentCall){
            // @TODO add system function calls

            case CREATEPROCESS -> OS.CreateProcess();
            case SWITCHPROCESS -> OS.switchProcess();
        }
    }
}
