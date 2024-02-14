public class PCB {
    static int nextpid = 0;
    private int pid;
    /** note: can wake up if time >= time to wake*/
    private long timeToWake; // Time until the process can be awoken
    private boolean isSleeping;

    private UserlandProcess up;

    private OS.Priority priority;

    /** creates thread, sets pid */
    PCB(UserlandProcess up){
        pid = nextpid++;
        this.up = up;
    }

    public OS.Priority getPriority() {
        return priority;
    }

    public void setPriority(OS.Priority priority) {
        this.priority = priority;
    }

    public boolean isSleeping() {
        return isSleeping;
    }

    /** Stop process and tuck it in and give it a kiss goodnight */
    public void isSleeping(boolean isSleeping){
        if(isSleeping) stop();
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

    long getTimeToWake(){
        return timeToWake;
    }

    @Override
    public String toString() {
        return up.toString();
    }
}
