public class PCB {
    static int nextpid = 0;
    int pid;

    UserlandProcess up;

    OS.Priority priority;

    /** creates thread, sets pid */
    PCB(UserlandProcess up){
        pid = nextpid++;
        this.up = up;
    }

    void stop(){
        up.stop();
    }

    boolean isDone(){
        return up.isDone();
    }

    void run(){

    }

    @Override
    public String toString() {
        return up.toString();
    }
}
