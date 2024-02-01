import java.util.ArrayList;

public class OS {
    /** Our Kernel <3 */
    private static Kernel kernel;

    /** Types of system calls */
    public enum CallType {
        CREATEPROCESS,
        SWITCHPROCESS,
        SHUTDOWN,
    }
    /** System call to be executed by the Kernel */
    static CallType currentCall;

    /** Parameters of our system call {@code currentCall} */
    static ArrayList<Object> params;
    /** return value of our system call {@code currentCall} */
    static Object returnValue;


    /**
     *  Starts and initializes OS, running {@code UserlandProcess} init
     *  initializes our {@code kernel} and runs {@code idleProcess}
     *
     * @param init process to be run at startup
     */
    public static void startup(UserlandProcess init) throws InterruptedException {
        OSPrinter.println("OS: starting up :)");
        kernel = new Kernel();
        params = new ArrayList<>();

        OSPrinter.print("OS: ");
        while(Kernel.thread.getState() != Thread.State.WAITING)
            OSPrinter.print("Waiting for Kernel thread... ");
        OSPrinter.println("\nOS: Kernel waiting to run\n");

        createProcess(init);
        createProcess(new IdleProcess());
    }

    /**
     * Sets our Shared Memory with our kernel - {@code CallType} and {@code params} - to request our {@code kernel} to create, and run, a new {@code UserlandProcess} up
     *
     * @param up {@code UserlandProcess} to be created
     */
    public static void createProcess(UserlandProcess up) {
        OSPrinter.printf("\nOS: Create process{%s} -> ", up);
        currentCall = CallType.CREATEPROCESS;

        params.clear();
        params.add(up); // sets params for kernel
        kernel.start();


//        waitForKernel();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

//    static void waitForKernel() {
//        while(Kernel.thread.getState() != Thread.State.WAITING){
//            try {
//                this.wait();
//            } catch (InterruptedException e) { }
//            System.out.print(".");
//        }
//        this.notify();
//    }


    /**
     * Tells our {@code KernelLand.Kernel} to switch process'
     */
    public static void switchProcess() {
        OSPrinter.print("\nOS: Switch process -> ");
        currentCall = CallType.SWITCHPROCESS;
        kernel.start();
    }
}
