import java.util.LinkedList;
import java.util.Timer; import java.util.TimerTask;

public class Scheduler {
    /** List of processes the KernelLand.Scheduler can access */
    private final LinkedList<UserlandProcess> processList; //@TODO should probably be a queue

    /** Schedules interrupts */
    private Timer interruptTimer;

    /** Time in until next interrupt, in ms */
    private long quantum = 250;

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

    /** Adds process to our {@code process list}, if nothing else is running it runs it
     * @return pid of the process */
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

        return processList.indexOf(up); // @TODO more efficient way please
    }

    /** Switches currently running process */
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
