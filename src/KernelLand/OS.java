package KernelLand;

import UserLand.IdleProcess;
import UserLand.UserlandProcess;
import Utility.OSPrinter;

import java.util.ArrayList;
import java.util.Arrays;

public class OS {
    /** Our Kernel <3 */
    private static final Kernel kernel = new Kernel();
    private static final Object lock = new Object();

    /** Types of system calls */
    public enum CallType {
        CREATEPROCESS,
        SWITCHPROCESS,
        SLEEP,
        OPEN,
        CLOSE,
        READ,
        SEEK,
        WRITE,
        SHUTDOWN,
    }

    /** The pid of the userland process that called an KernelLand.OS method */
    static int callerPid;
    /** System call to be executed by the KernelLand.Kernel */
    static CallType currentCall;

    /** Parameters of our system call {@code currentCall} */
    static ArrayList<Object> params;
    /** return value of our system call {@code currentCall} */
    static Object returnValue;

    /** Priority of a {@code UserLand.UserlandProcess}
     * determines runtime priority of the process
     * */
    public enum Priority {
        REALTIME,
        INTERACTIVE,
        BACKGROUND,
    }


    /**
     *  Starts and initializes KernelLand.OS, running {@code UserLand.UserlandProcess} init
     *  initializes our {@code kernel} and runs {@code idleProcess}
     *
     * @param init process to be run at startup
     */
    public static void startup(UserlandProcess init) {
        OSPrinter.println("OS: starting up :)");
        params = new ArrayList<>();

        OSPrinter.println("\nOS: KernelLand.Kernel waiting to run\n");

        createProcess(init);
        createProcess(new IdleProcess());

        OSPrinter.println("\nOS: startup complete\n");
    }

    /**
     * Tells our KernelLand.Kernel to create a new {@code UserLand.UserlandProcess} with default priority interactive
     * Sets our Shared Memory with our kernel - {@code CallType} and {@code params} - to request our {@code kernel} to create, and run, a new {@code UserLand.UserlandProcess} up
     *
     * @param up {@code UserLand.UserlandProcess} to be created
     */
    public static void createProcess(UserlandProcess up) {
        createProcess(up, Priority.INTERACTIVE);
    }
    /**
     * Tells our KernelLand.Kernel to create a new {@code UserLand.UserlandProcess} with set priority
     * Sets the Shared Memory with our kernel - {@code CallType} and {@code params} - to request our {@code kernel} to create, and run, a new {@code UserLand.UserlandProcess} up
     *
     * @param up {@code UserLand.UserlandProcess} to be created
     *
     */
    public static void createProcess(UserlandProcess up, Priority priority) // TODO implement create process with priority
    {
        OSPrinter.printf("\nKernelLand.OS: Create process{%s} -> ", up);

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
        PCB switchedPCB = Kernel.scheduler.runningPCB; // Process to be switched from

        OSPrinter.print("\nOS: Switch process -> ");
        currentCall = CallType.SWITCHPROCESS;
        kernel.start();
        waitForKernel();

        // no need for runningPCB.stop because process stops on cooperate
    }

    public static void sleep(int milliseconds) {
        PCB runningPCB = Kernel.scheduler.runningPCB;

        OSPrinter.printf("\nOS: Sleep{%s} -> ", runningPCB);

        currentCall = CallType.SLEEP;
        params.clear();
        params.add(milliseconds);
        kernel.start();

        waitForKernel();

        runningPCB.stop();
    }

    /** Sleeps currently running process and switches to the next process */
    public static void sleep(int milliseconds, int identity) {
        PCB sleepingPCB = Kernel.scheduler.runningPCB; // Process to be slept

        OSPrinter.printf("\nOS: Sleep{%s} -> ", sleepingPCB);

        currentCall = CallType.SLEEP;
        callerPid = identity;
        params.clear();
        params.add(milliseconds);
        kernel.start();

        waitForKernel();

        sleepingPCB.stop();
    }

    public static int open(String s) {
        OSPrinter.printf("\nOS: open{%s} -> ", Kernel.scheduler.runningPCB);

        callKernel(CallType.OPEN, s);
        return (int)returnValue;
    }

    public static void close(int id) {
        OSPrinter.printf("\nOS: close{%s} -> ", Kernel.scheduler.runningPCB);

        callKernel(CallType.CLOSE, id);
    }

    public static byte[] read(int id, int size) {
        OSPrinter.printf("\nOS: read{%s} -> ", Kernel.scheduler.runningPCB);

        callKernel(CallType.READ, id, size);

        return (byte[]) returnValue;
    }

    public static void seek(int id, int to) {
        OSPrinter.printf("\nOS: seek{%s} -> ", Kernel.scheduler.runningPCB);

        callKernel(CallType.SEEK, id, to);
    }

    public static int write(int id, byte[] data) {
        OSPrinter.printf("\nOS: write{%s} -> ", Kernel.scheduler.runningPCB);

        callKernel(CallType.WRITE, id, data);
        return (int)returnValue;
    }

    /**
     * Set {@code currentCall} and add parameters to shared memory {@code params}
     * then, call the kernel and wait for execution.
     *
     * @param callType the call type
     * @param parameters the parameters
     */
    private static void callKernel(CallType callType, Object... parameters) {
        currentCall = callType;

        params.clear();
        params.addAll(Arrays.asList(parameters));
        kernel.start();

        waitForKernel();
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

    /** notify the KernelLand.OS to continue execution */
    static void notifyComplete() {
        lock.notify();
    }

    /** return {@code lock} */
    static Object getLock(){
        return lock;
    }

    public static Kernel getKernel() { return kernel; }
}
