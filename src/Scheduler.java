import java.time.Clock;
import java.util.*;

public class Scheduler {
    /** Queue of processes the Scheduler can access with real time priority */
    private final Queue<PCB> realTime;
    /** Queue of processes the Scheduler can access with interactive priority */
    private final Queue<PCB> interactive;
    /** Queue of processes the Scheduler can access with background priority */
    private final Queue<PCB> background;
    /** Queue of sleeping processes */
    private final PriorityQueue<PCB> sleeping;

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
        sleeping = new PriorityQueue<>(Comparator.comparingLong(PCB::getTimeToWake));

        Timer interruptTimer = new Timer("Interrupt");

        TimerTask requestStop = new TimerTask() {
            @Override
            public void run() {
                if(runningPCB != null)
                    runningPCB.requestStop(); // will switch processes
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
        newPCB.setPriority(OS.Priority.INTERACTIVE);
        interactive.add(newPCB);

        if(runningPCB == null)
            runningPCB = newPCB;
        else if(runningPCB.isDone())
            switchProcess();

        OSPrinter.println("Interactive process list: " + interactive); OSPrinter.println("");

        return up.pid;
    }

    /** Wake sleeping processes with time to wake <= current time */
    private void wakeProcess() {
        PCB peek;
        while ((peek = sleeping.peek()) != null)
        {
            if (peek.timeToWake <= clock.millis()) {
                /* Wake process */
                Queue<PCB> priority = getPriorityQueue(peek);
                priority.add(sleeping.poll());
                OSPrinter.println("Waking process: " + peek);
                OSPrinter.println("Interactive process list: " + interactive);
                OSPrinter.println("Sleeping process list: " + sleeping);
            }
            else break;
        }
    }

    /** Switches currently running process.
     * Awakens sleeping processes */
    void switchProcess()
    {

        OSPrinter.println("Scheduler: switch process");

        /* Get the processes priority */
        Queue<PCB> priority = getPriorityQueue(runningPCB);

        priority.remove();
        if(!runningPCB.isDone() && !runningPCB.isSleeping())
            priority.add(runningPCB);

        runningPCB = priority.peek();

        OSPrinter.println(runningPCB.getPriority().toString() + " process list: " + priority);
        OSPrinter.println("Running PCB: " + runningPCB);

        wakeProcess(); // TODO the process somehow sleeps itself when it's switched to next
    }

    private Queue<PCB> getPriorityQueue(PCB pcb) {
        /* Get the processes priority */
        Queue<PCB> priority;
        switch (runningPCB.getPriority()) {
            case REALTIME -> priority = realTime;
            case INTERACTIVE -> priority = interactive;
            case BACKGROUND -> priority = background;
            default -> throw new RuntimeException("PCB priority not set");
        }
        return priority;
    }

    /** Stop currentPCB, add it to the {@code sleeping} queue and set the next process to run */
     void sleep(int milliseconds) {
        if(runningPCB.isSleeping())
            throw new RuntimeException("Sleeping process called function");
//        if(runningPCB.getPid() != OS.callerPid){ //TODO this is a band-aid and doesnt even work every time
//            OSPrinter.print("Non-running process called function");
//            return;
//        }

        OSPrinter.printf("Scheduler{%s}: sleep\n", runningPCB);

        runningPCB.isSleeping(true);
        runningPCB.setTimeToWake(clock.millis() + milliseconds);
        sleeping.add(runningPCB);

        switchProcess(); // take it off the list and run next process

        OSPrinter.println("Sleeping process list: " + sleeping);
        OSPrinter.println("");
    }
}

