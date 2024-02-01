import java.util.LinkedList;
import java.util.Timer; import java.util.TimerTask;

public class Scheduler {
    /** List of processes the KernelLand.Scheduler can access */
    private LinkedList<UserlandProcess> processList; //@TODO should probably be a queue

    /** Schedules interrupts */
    private Timer interruptTimer;

    /** Time in until next interrupt, in ms */
    long quantum = 250;

    /** Currently running process */
    public UserlandProcess currProcess;


    /** Creates a new KernelLand.Scheduler */
    Scheduler()
    {
        processList = new LinkedList<>();
        interruptTimer = new Timer("Interrupt");

        TimerTask requestStop = new TimerTask() {
            @Override
            public void run() {
                if(currProcess != null)
                    currProcess.requestStop();
            }
        };

        interruptTimer.scheduleAtFixedRate(requestStop, 1000, quantum);
    }

    public int createProcess(UserlandProcess up)
    {
        OSPrinter.println("Scheduler: Create process");

        processList.add(up);

        if(currProcess == null)
            currProcess = up;
        else if(currProcess.isDone())
            switchProcess();

        OSPrinter.println("process list: " + processList);
        OSPrinter.println("");
        return 0;
    }

    public void switchProcess()
    {
        OSPrinter.println("Scheduler: switch process");

        currProcess.stop();
        processList.remove(currProcess);
        if(!currProcess.isDone())
            processList.addLast(currProcess);

        currProcess = processList.getFirst();

        OSPrinter.println("process list: " + processList);
        OSPrinter.println("");
    }
}
