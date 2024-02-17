public class PCB {
    static int nextpid = 0;
    private int pid;
    /** note: can wake up if time >= time to wake*/
    long timeToWake; // Time until the process can be awoken
    private boolean isSleeping = false;

    private UserlandProcess up;

    private OS.Priority priority;

    /** creates thread, sets pid */
    PCB(UserlandProcess up){
        pid = nextpid++;
        this.up = up;
    }

    public boolean isSleeping() {
        return isSleeping;
    }

    /** Stop process status to sleeping and stop process */
    public void isSleeping(boolean isSleeping){
        this.isSleeping = isSleeping;
        if(isSleeping) requestStop();
    }

//    void stop(){
//        up.stop();
//    }

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
    }

    @Override
    public String toString() {
        return up.toString();
    }
}
