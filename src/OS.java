import java.util.ArrayList;

public class OS {
    /** Our Kernel <3 */
    private static Kernel kernel;
    private static final Object lock = new Object();

    /** Types of system calls */
    public enum CallType {
        CREATEPROCESS,
        SWITCHPROCESS,
        SLEEP,
        SHUTDOWN,
    }

    /** The pid of the userland process that called an OS method */
    static int callerPid;
    /** System call to be executed by the Kernel */
    static CallType currentCall;

    /** Parameters of our system call {@code currentCall} */
    static ArrayList<Object> params;
    /** return value of our system call {@code currentCall} */
    static Object returnValue;

    /** Priority of a {@code UserlandProcess}
     * determines runtime priority of the process
     * */
    public enum Priority {
        REALTIME,
        INTERACTIVE,
        BACKGROUND,
    }


    /**
     *  Starts and initializes OS, running {@code UserlandProcess} init
     *  initializes our {@code kernel} and runs {@code idleProcess}
     *
     * @param init process to be run at startup
     */
    public static void startup(UserlandProcess init) {
        OSPrinter.println("OS: starting up :)");
        kernel = new Kernel();
        params = new ArrayList<>();

        waitForKernel();
        OSPrinter.println("\nOS: Kernel waiting to run\n");

        createProcess(init);
        createProcess(new IdleProcess());

        OSPrinter.println("\nOS: startup complete\n");
    }

    /**
     * Tells our Kernel to create a new {@code UserlandProcess} with default priority interactive
     * Sets our Shared Memory with our kernel - {@code CallType} and {@code params} - to request our {@code kernel} to create, and run, a new {@code UserlandProcess} up
     *
     * @param up {@code UserlandProcess} to be created
     */
    public static void createProcess(UserlandProcess up) {
        createProcess(up, Priority.INTERACTIVE);
    }
    /**
     * Tells our Kernel to create a new {@code UserlandProcess} with set priority
     * Sets the Shared Memory with our kernel - {@code CallType} and {@code params} - to request our {@code kernel} to create, and run, a new {@code UserlandProcess} up
     *
     * @param up {@code UserlandProcess} to be created
     *
     */
    public static void createProcess(UserlandProcess up, Priority priority) // TODO implement create process with priority
    {
        OSPrinter.printf("\nOS: Create process{%s} -> ", up);
        currentCall = CallType.CREATEPROCESS;

        params.clear();
        params.add(up);
        params.add(priority);
        kernel.start();

        waitForKernel();
    }

    /**
     * Tells our {@code kernel} to switch process'
     */
    public static void switchProcess() {
        OSPrinter.print("\nOS: Switch process -> ");
        currentCall = CallType.SWITCHPROCESS;
        kernel.start();
        waitForKernel();
    }

    static void sleep(int milliseconds, int identity) {
        PCB runningPCB = Kernel.scheduler.runningPCB;
        OSPrinter.printf("\nOS: Sleep{%s} -> ", runningPCB);
        currentCall = CallType.SLEEP;
        callerPid = identity;

        params.clear();
        params.add(milliseconds);
        kernel.start();

        waitForKernel();

        runningPCB.stop();
    }

    /** Wait for the {@code kernel} thread to finish the execution */
    static void waitForKernel() {
        synchronized (lock){
            try {
                lock.wait();
            } catch (InterruptedException e) {
                OSPrinter.println("Thread interrupted");
            }
        }
    }

    /** notify the OS to continue execution */
    static void notifyComplete() {
        lock.notify();
    }

    /** return {@code lock} */
    static Object getLock(){
        return lock;
    }



}
