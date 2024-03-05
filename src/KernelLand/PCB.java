package KernelLand;

import UserLand.UserlandProcess;

public class PCB {
    static int nextpid = 0;
    private final int pid;
    /** Time until the process can be awoken note: can wake up if time >= time to wake */
    long timeToWake;
    private boolean isSleeping = false;

    private final UserlandProcess up;

    private OS.Priority priority;

    private final int[] deviceIDs = new int[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1};

    /** creates thread, sets pid */
    PCB(UserlandProcess up) {
        pid = nextpid++;
        this.up = up;
    }

    public UserlandProcess getUp() {
        return up;
    }

    public int getPid() {
        return pid;
    }

    public boolean isSleeping() {
        return isSleeping;
    }

    /** Stop process status to sleeping and stop process */
    public void isSleeping(boolean isSleeping){
        this.isSleeping = isSleeping;
    }

    void stop(){
        up.stop();
    }

    void requestStop(){
        up.requestStop();
    }

    boolean isDone(){
        return up.isDone();
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

    public OS.Priority getPriority() {
        return priority;
    }

    public void setPriority(OS.Priority priority) {
        this.priority = priority;
        up.setPriority(priority); //TODO for testing
    }

    public int[] getDeviceIDs() { return deviceIDs; }

    @Override
    public String toString() {
        if(isSleeping)
            return String.format("%s, pid:%s, Time to wake:%s",
                    up.toString(), pid, timeToWake);
        return String.format( "%s pid:%s",
                up.toString(), pid);
    }
}
