import java.time.Clock;
import java.util.*;

public class Scheduler {
    /** List of processes the Scheduler can access with real time priority */
    private final Queue<PCB> realTime;
    /** List of processes the Scheduler can access with interactive priority */
    private final Queue<PCB> interactive;
    /** List of processes the Scheduler can access with background priority */
    private final Queue<PCB> background;

    private final Queue<PCB> sleeping;

    private final Clock clock = Clock.systemUTC();

    /** Time in until next interrupt, in ms */
    private final long quantum = 250;

    /** Currently running process */
    public PCB runningPCB;

    /** Creates a new KernelLand.Scheduler */
    Scheduler()
    {
        realTime = new LinkedList<>();
        interactive = new LinkedList<>();
        background = new LinkedList<>();
        sleeping = new LinkedList<>();

        Timer interruptTimer = new Timer("Interrupt");

        TimerTask requestStop = new TimerTask() {
            @Override
            public void run() {
                if(runningPCB != null)
                    runningPCB.up.requestStop(); // will switch process'
            }
        };

        interruptTimer.scheduleAtFixedRate(requestStop, 1000, quantum);
    }

    /** Adds process to our {@code process list}, if nothing else is running it runs it
     * @return pid of the process */
    public int createProcess(UserlandProcess up)
    {
        OSPrinter.println("Scheduler: Create process");

        PCB newPCB = new PCB(up);
        newPCB.priority = OS.Priority.INTERACTIVE;
        interactive.add(newPCB);

        if(runningPCB == null)
            runningPCB = newPCB;
        else if(runningPCB.isDone())
            switchProcess();

        OSPrinter.println("Interactive process list: " + interactive); OSPrinter.println("");

        return up.pid;
    }

    /** Switches currently running process
     * Awakens sleeping processes */
    public void switchProcess()
    {
        if(runningPCB == null) return; // no need to switch

        OSPrinter.println("Scheduler: switch process");

        /* Get the processes priority */
        Queue<PCB> priority;
        String priorityName;
        switch (runningPCB.priority) {
            case REALTIME -> { priority = realTime; priorityName = "Real time"; }
            case INTERACTIVE -> { priority = interactive; priorityName = "Interactive"; }
            case BACKGROUND -> { priority = background; priorityName = "background"; }
            default -> throw new RuntimeException("PCB priority not set");
        }

        runningPCB.stop();
        priority.remove();
        if(!runningPCB.isDone())
            priority.add(runningPCB);

        runningPCB = priority.peek();

        OSPrinter.println(priorityName + " process list: " + priority);
        OSPrinter.println("");
    }

    public void sleep(int milliseconds)
    {
        OSPrinter.println("Scheduler: sleep");


//        runningPCB.isDone(true);
        switchProcess();
    }
}
