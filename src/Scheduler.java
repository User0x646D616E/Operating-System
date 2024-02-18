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
            public void run()
            {
                demote();

                if(runningPCB != null)
                    runningPCB.requestStop(); // will switch processes
            }
        };

        interruptTimer.scheduleAtFixedRate(requestStop, 1000, quantum);
    }

    private void demote(){
        if(runningPCB.getUp().timeoutCounter >= 5) {
            if(runningPCB.getPriority() == OS.Priority.REALTIME)
                runningPCB.setPriority(OS.Priority.INTERACTIVE);
            else runningPCB.setPriority(OS.Priority.BACKGROUND);
            runningPCB.getUp().timeoutCounter = 0;
        }
    }


    /** Adds process to our {@code process list}, if nothing else is running it runs it
     * @return pid of the process */
    public int createProcess(UserlandProcess up, OS.Priority priority)
    {
        OSPrinter.println("Scheduler: Create process");

        Queue<PCB> priorityQueue;
        switch (priority){
            case REALTIME -> priorityQueue = realTime;
            case INTERACTIVE -> priorityQueue = interactive;
            case BACKGROUND -> priorityQueue = background;
            default -> throw new RuntimeException("PCB priority not set");
        }

        PCB newPCB = new PCB(up);
        newPCB.setPriority(priority);
        priorityQueue.add(newPCB);

        if(runningPCB == null)
            runningPCB = newPCB;
        else if(runningPCB.isDone())
            switchProcess();

        OSPrinter.println( "Real time process list: " + realTime);
        OSPrinter.println( "Interactive process list: " + interactive);
        OSPrinter.println( "Background process list: " + background);

        return up.pid;
    }

    /** Wake sleeping processes with time to wake <= current time */
    private void wakeProcess() {
        PCB peek;
        while ((peek = sleeping.peek()) != null)
        {
            if (peek.timeToWake <= clock.millis()) {
                /* Wake process */
                peek.isSleeping(false);
                Queue<PCB> priority = getPriorityQueue(peek);
                priority.add(sleeping.poll());
                OSPrinter.println("Waking process: " + peek);
                OSPrinter.println(peek.getPriority() + " process list: " + priority);
                OSPrinter.println("Sleeping process list: " + sleeping);
            }
            else break;
        }
    }

    /** Switches currently running process.
     * Awakens sleeping processes */
    void switchProcess()
    {
        wakeProcess();

        OSPrinter.println("Scheduler: switch process");

        /* Get the processes priority */
        Queue<PCB> priority = choosePriority();
        if(priority == null) return; // no need to switch

        PCB newPCB = priority.remove();
        if(!runningPCB.isDone() && !runningPCB.isSleeping())
            priority.add(newPCB);

        runningPCB = priority.peek();

        OSPrinter.println("Real time process list: " + realTime);
        OSPrinter.println("Interactive process list: " + interactive);
        OSPrinter.println("Background process list: " + background);

        OSPrinter.println("Running PCB: " + runningPCB);
    }

    private Queue<PCB> choosePriority()
    {
        Random random = new Random();
        int totalWeight = 6 + 3 + 1;
        int randomNumber = random.nextInt(totalWeight) + 1;

        if (randomNumber <= 6 && !realTime.isEmpty()) {
            return realTime;
        } else if (randomNumber <= 9 && !interactive.isEmpty()) {
            return interactive;
        } else if (!background.isEmpty()) {
            return background;
        } else if (!realTime.isEmpty()) {
            return realTime;
        } else if (!interactive.isEmpty()) {
            return interactive;
        } else {
            return null; // All lists are empty
        }
    }

//    private Queue<PCB> choosePriority()
//    {
//        Random rand = new Random();
//        if(!realTime.isEmpty() && !interactive.isEmpty() && !background.isEmpty()) {
//            int randInt = rand.nextInt(10) + 1;
//
//            if(randInt == 1) return background;
//            if(randInt <= 4) return interactive;
//            else return realTime;
//        }
//        if(!realTime.isEmpty() && interactive.isEmpty() && !background.isEmpty()) {
//            int randInt = rand.nextInt(10) + 1;
//
//            if(randInt == 1) return background;
//            else return realTime;
//        }
//        if(realTime.isEmpty() && !interactive.isEmpty() && !background.isEmpty()) {
//            int randInt = rand.nextInt(4) + 1;
//
//            if(randInt == 1) return background;
//            else return interactive;
//        }
//        if(realTime.isEmpty() && interactive.isEmpty() && !background.isEmpty())
//            return background;
//        else return null;
//    }



    /** returns the priority queue that pcb belongs to */
    private Queue<PCB> getPriorityQueue(PCB pcb) {
        /* Get the processes priority */
        Queue<PCB> priority;
        switch (pcb.getPriority()) {
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

