import java.time.Clock;
import java.util.*;
import java.util.function.Predicate;

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

    private void wakeProcess() {
        while(true){
            PCB peek = sleeping.peek();
            if(peek == null) return;

            if(peek.timeToWake <= clock.millis())
                sleeping.poll();
            else return;
        }
//        sleeping.removeIf(); // maybe
    }

    /** Switches currently running process.
     * Awakens sleeping processes */
    public void switchProcess()
    {
//        wakeProcess();

        OSPrinter.println("Scheduler: switch process");

        runningPCB.requestStop();

        /* Get the processes priority */
        Queue<PCB> priority;
        String priorityName;
        switch (runningPCB.getPriority()) {
            case REALTIME -> { priority = realTime; priorityName = "Real time"; }
            case INTERACTIVE -> { priority = interactive; priorityName = "Interactive"; }
            case BACKGROUND -> { priority = background; priorityName = "background"; }
            default -> throw new RuntimeException("PCB priority not set");
        }

        priority.remove();
        if(!runningPCB.isDone() && !runningPCB.isSleeping())
            priority.add(runningPCB);

        runningPCB = priority.peek();

        OSPrinter.println(priorityName + " process list: " + priority);
    }

    /** Stop currentPCB, add it to the {@code sleeping} queue and set the next process to run */
    public void sleep(int milliseconds)
    {
        if(runningPCB.getPriority() == OS.Priority.SLEEPING)
            return; // TODO make new exception 'sleeping process called function'

        OSPrinter.printf("Scheduler{%s}: sleep\n", runningPCB);

        runningPCB.isSleeping(true);
        runningPCB.setTimeToWake(clock.millis() + milliseconds);
        sleeping.add(runningPCB);

        switchProcess(); // take it off the list and run next process

        OSPrinter.println("Sleeping process list: " + sleeping);
        OSPrinter.println("");
    }

}

