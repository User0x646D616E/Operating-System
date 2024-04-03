package KernelLand;

import UserLand.UserlandProcess;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;


public class PCB {
    private final UserlandProcess up;
    private final int pid;
    static int nextpid = 0;
    String name;

    private final Queue<Message> messageQueue;

    /* SCHEDULING */
    /** Time until the process can be awoken note: can wake up if time >= timeToWake */
    long timeToWake;
    private boolean isSleeping = false;
    private boolean isWaiting = false;
    private OS.Priority priority;

    /* DEVICES */
    private final int[] deviceIDs = new int[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1};

    /* MEMORY */
    private int availableMemory;

    static public final int PROCESS_PAGE_COUNT = 100;
    /** Map of virtual pages to physical pages where the array index indicates the virtual page and the entry
     * represents the physical mapping, or -1 if no mapping exists */
    int[] virtualPages = new int[PROCESS_PAGE_COUNT];


    /** creates thread, sets pid */
    PCB(UserlandProcess up) {
        Arrays.fill(virtualPages, -1);

        pid = nextpid++;
        this.up = up;
        name = up.getClass().getSimpleName();
        messageQueue = new LinkedList<>();
        availableMemory = 0;
    }

    public UserlandProcess getUp() {
        return up;
    }

    public int getPid() {
        return pid;
    }

    void stop(){
        up.stop();
    }

    void requestStop(){
        up.requestStop();
    }

    void run(){
        up.start();
    }

    void setTimeToWake(long timeToWake){
        this.timeToWake = timeToWake;
    }

    long getTimeToWake() {
        return timeToWake;
    }

    public void setPriority(OS.Priority priority) {
        this.priority = priority;
        up.setPriority(priority);
    }

    public OS.Priority getPriority() {
        return priority;
    }

    public Queue<Message> getMessageQueue() {
        return messageQueue;
    }

    public int[] getDeviceIDs() { return deviceIDs; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        if(isSleeping)
            return String.format("%s, pid:%s, Time to wake:%s", name, pid, timeToWake);
        return String.format( "%s pid:%s", name, pid);
    }

    public boolean isStopped() {
        return  up.isStopped();
    }

    public boolean isSleeping() {
        return isSleeping;
    }

    /** Set sleeping status */
    public void isSleeping(boolean isSleeping){
        this.isSleeping = isSleeping;
    }

    public boolean isWaiting() {
        return isWaiting;
    }

    public void isWaiting(boolean isWaiting) {
        this.isWaiting = isWaiting;
    }

    boolean isDone(){
        return up.isDone();
    }

    public int[] getVirtualPages() {
        return virtualPages;
    }

    public int getAvailableMemory() {
        return availableMemory;
    }

    public void setAvailableMemory(int availableMemory) {
        this.availableMemory = availableMemory;
    }
}
