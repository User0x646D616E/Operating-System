package KernelLand;

import java.time.Clock;
import java.util.*;

import UserLand.UserlandProcess;
import Utility.OSPrinter;

public class Scheduler {
    /** Queue of processes the KernelLand.Scheduler can access with real time priority */
    private final Queue<PCB> realTime;
    /** Queue of processes the KernelLand.Scheduler can access with interactive priority */
    private final Queue<PCB> interactive;
    /** Queue of processes the KernelLand.Scheduler can access with background priority */
    private final Queue<PCB> background;
    /** Queue of sleeping processes */
    private final PriorityQueue<PCB> sleeping;

    private final Clock clock = Clock.systemUTC();

    /** Currently running process */
    public PCB runningPCB;

    /** Creates a new KernelLand.KernelLand.Scheduler */

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
                demote(5);

                if(runningPCB.isDone())
                    OS.switchProcess();
                if(runningPCB != null)
                    runningPCB.requestStop(); // will switch processes
            }
        };

        /* Time in until next interrupt, in ms */
        long quantum = 250;
        interruptTimer.scheduleAtFixedRate(requestStop, 1000, quantum);
    }

    /** Demote a thread after its {@code getTimeoutCounter} is greater than MAX_TIMEOUT */
    private void demote(final int MAX_TIMEOUT) {
        if(runningPCB == null) return;

        if(runningPCB.getUp().getTimeoutCounter() >= MAX_TIMEOUT) {
            if(runningPCB.getPriority() == OS.Priority.REALTIME)
                runningPCB.setPriority(OS.Priority.INTERACTIVE);
            else runningPCB.setPriority(OS.Priority.BACKGROUND);
            runningPCB.getUp().setTimeoutCounter(0);
        }
    }


    /** Adds process to our {@code process list}, if nothing else is running it runs it
     * @return pid of the process */
    public int createProcess(UserlandProcess up, OS.Priority priority)
    {
        OSPrinter.println("KernelLand.Scheduler: Create process");

        Queue<PCB> priorityQueue;
        switch (priority){
            case REALTIME -> priorityQueue = realTime;
            case INTERACTIVE -> priorityQueue = interactive;
            case BACKGROUND -> priorityQueue = background;
            default -> throw new RuntimeException("KernelLand.PCB priority not set");
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

    /** Wake sleeping processes when  */
    private void wakeProcess() {
        PCB peek;
        while ((peek = sleeping.peek()) != null)
        {
            long currentTime = clock.millis();
            if(peek.timeToWake <= currentTime) {
                /* Wake process */
                peek.isSleeping(false);
                Queue<PCB> priority = getPriorityQueue(peek);
                priority.add(sleeping.poll());
                OSPrinter.println("Waking process: " + peek + "time: " + currentTime);
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

        OSPrinter.println("KernelLand.Scheduler: switch process");

        /* Get the processes priority */
        Queue<PCB> priority = choosePriority();
        if(priority == null) return; // no need to switch

        PCB newPCB = priority.remove();
        if(runningPCB.isDone()) {
            /* Close Devices */
            for(int id : runningPCB.getDeviceIDs())
                Kernel.vfs.close(id);
        }
        else if(!runningPCB.isSleeping())
            priority.add(newPCB);

        runningPCB = priority.peek();

        OSPrinter.println("Real time process list: " + realTime);
        OSPrinter.println("Interactive process list: " + interactive);
        OSPrinter.println("Background process list: " + background);

        OSPrinter.println("Running KernelLand.PCB: " + runningPCB);
    }

    private Queue<PCB> choosePriority()
    {
        Random random = new Random();
        int totalWeight = 6 + 3 + 1;
        int randomNumber = random.nextInt(totalWeight) + 1;

        if (randomNumber <= 6 && !realTime.isEmpty())
            return realTime;
        else if (randomNumber <= 9 && !interactive.isEmpty())
            return interactive;
        else if (!background.isEmpty())
            return background;
        else if (!realTime.isEmpty())
            return realTime;
        else if (!interactive.isEmpty())
            return interactive;
        else return null; // All lists are empty
    }

//    private Queue<KernelLand.PCB> choosePriority()
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
            default -> throw new RuntimeException("KernelLand.PCB priority not set");
        }
        return priority;
    }

    /** Stop currentPCB, add it to the {@code sleeping} queue and set the next process to run */
     void sleep(int milliseconds) {
        if(runningPCB.isSleeping())
            throw new RuntimeException("Sleeping process called function");
////        if(runningPCB.getPid() != KernelLand.OS.callerPid){ //TODO this is a band-aid and doesn't even work every time
////            Utility.OSPrinter.print("No running process called function");
//            return;
//        }

        OSPrinter.printf("KernelLand.Scheduler{%s}: sleep\n", runningPCB);

        runningPCB.isSleeping(true);
        runningPCB.setTimeToWake(clock.millis() + milliseconds);
        sleeping.add(runningPCB);

        switchProcess(); // take it off the list and run next process

        OSPrinter.println("Sleeping process list: " + sleeping);
        OSPrinter.println("");
    }

    public PCB getRunningPCB() { return runningPCB; }
}

