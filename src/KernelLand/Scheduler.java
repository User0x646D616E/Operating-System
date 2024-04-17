package KernelLand;

import java.time.Clock;
import java.util.*;

import UserLand.UserlandProcess;
import Utility.OSPrinter;

import static KernelLand.Kernel.*;

public class Scheduler {
    /** Queue of processes the KernelLand.Scheduler can access with real time priority */
    private final Queue<PCB> realTime;
    /** Queue of processes the KernelLand.Scheduler can access with interactive priority */
    private final Queue<PCB> interactive;
    /** Queue of processes the KernelLand.Scheduler can access with background priority */
    private final Queue<PCB> background;
    /** PriorityQueue of sleeping processes */
    private final PriorityQueue<PCB> sleeping;

    /** Hash map of pid to PCB of processes that are waiting for a message */
    private final HashMap<Integer, PCB> waiting;

    /** Maps each processes pid to its respective {@code PCB} */
    HashMap<Integer, PCB> processPIDs;

    private final Clock clock = Clock.systemUTC();

    /** Currently running process */
    public PCB runningPCB;

    /** Creates a new Scheduler */
    Scheduler()
    {
        realTime = new LinkedList<>();
        interactive = new LinkedList<>();
        background = new LinkedList<>();
        sleeping = new PriorityQueue<>(Comparator.comparingLong(PCB::getTimeToWake));

        waiting = new HashMap<>();
        processPIDs = new HashMap<>();

        Timer interruptTimer = new Timer("Interrupt");

        TimerTask requestStop = new TimerTask() {
            @Override
            public void run()
            {
                demote();
                UserlandProcess.clearTlb();

                if(runningPCB.isDone())
                    OS.switchProcess();
                else if(runningPCB.isStopped()) // fix for when the only process on the list calls switch process
                    runningPCB.run();           // (switches to itself and then stops)
                else if(runningPCB != null)
                    runningPCB.requestStop(); // will switch processes
            }
        };

        /* Time in until next interrupt, in ms */
        long quantum = 10;
        interruptTimer.scheduleAtFixedRate(requestStop, 1000, quantum);
    }

    /** Demote a thread after its {@code getTimeoutCounter} is greater than MAX_TIMEOUT */
    private void demote() {
        if(runningPCB == null) return;

        if(runningPCB.getUp().getTimeoutCounter() >= 5) {
            if(runningPCB.getPriority() == OS.Priority.REALTIME) {
                realTime.remove(runningPCB);
                runningPCB.setPriority(OS.Priority.INTERACTIVE);
            }
            else {
                interactive.remove(runningPCB);
                runningPCB.setPriority(OS.Priority.BACKGROUND);
            }
            runningPCB.getUp().setTimeoutCounter(0);
        }
    }


    /** Adds process to our {@code process list}, if nothing else is running it runs it
     * @return pid of the process */
    public int createProcess(UserlandProcess up, OS.Priority priority)
    {
        OSPrinter.println("KernelLand.Scheduler: Create process");

        Queue<PCB> priorityQueue;
        switch(priority) {
            case REALTIME -> priorityQueue = realTime;
            case INTERACTIVE -> priorityQueue = interactive;
            case BACKGROUND -> priorityQueue = background;
            default -> throw new RuntimeException("KernelLand.PCB priority not set");
        }

        PCB newPCB = new PCB(up);
        processPIDs.put(newPCB.getPid(), newPCB); // add pid to list
        newPCB.setPriority(priority);
        priorityQueue.add(newPCB);

        if(runningPCB == null)
            runningPCB = newPCB;
        else if(runningPCB.isDone())
            switchProcess();

        OSPrinter.println( "Real time process list: " + realTime);
        OSPrinter.println( "Interactive process list: " + interactive);
        OSPrinter.println( "Background process list: " + background);
        OSPrinter.println("" + processPIDs.keySet() + processPIDs.values());

        return up.pid;
    }


    /** Switches currently running process.
     * Awakens sleeping processes */
    void switchProcess()
    {
        OSPrinter.println("KernelLand.Scheduler: switch process");

        Queue<PCB> runningPriority = getPriorityQueue(runningPCB);
        Queue<PCB> newPriority = choosePriority();

        runningPriority.remove(runningPCB);

        if(runningPCB.isDone())
           endProcess();
        else if(!runningPCB.isSleeping() && !runningPCB.isWaiting()) // if not sleeping and not waiting, then
            runningPriority.add(runningPCB);                                    // add back to the list

        if(newPriority == null) return; // no need to switch
        runningPCB = newPriority.peek();

        OSPrinter.println("Real time process list: " + realTime);
        OSPrinter.println("Interactive process list: " + interactive);
        OSPrinter.println("Background process list: " + background);

        OSPrinter.println("Running PCB: " + runningPCB);

        wakeProcess();
    }

    private void endProcess() {
        /* Close Devices */
        for(int id : runningPCB.getDeviceIDs())
            Kernel.vfs.close(id);

        /* Free Memory */
        for(int i = 0; i < runningPCB.getAvailableMemory()/PAGE_SIZE; i++)
            pageUseMap[runningPCB.virtualPages[i]] = false;

        processPIDs.remove(runningPCB.getPid());
    }

    /** Stop currentPCB, add it to the {@code sleeping} queue and set the next process to run */
    void sleep(int milliseconds) {
        if(runningPCB.isSleeping())
            throw new RuntimeException("Sleeping process called function");

        OSPrinter.printf("Scheduler{%s}: sleep\n", runningPCB);

        runningPCB.isSleeping(true);
        runningPCB.setTimeToWake(clock.millis() + milliseconds);
        sleeping.add(runningPCB);

        switchProcess(); // take it off the list and run next process

        OSPrinter.println("Sleeping process list: " + sleeping);
        OSPrinter.println("");
    }

    /**
     * Removes process from running queue and puts it in waiting queue until a message is received
     *
     * @return message the message you have been waiting for.
     * Null if a message has not yet been received
     */
     Message waitForMessage() {
         OSPrinter.printf("Scheduler{%s}: waitForMessage\n", runningPCB);

        Queue<Message> messages = runningPCB.getMessageQueue();

        if(!messages.isEmpty()) { // we have a message
            OSPrinter.println("Waiting process list: " + waiting);
            OSPrinter.println("");

            return messages.poll();
        }

        runningPCB.isWaiting(true);
        waiting.put(runningPCB.getPid(), runningPCB);

        switchProcess();

        OSPrinter.println("Waiting process list: " + waiting);
        OSPrinter.println("");
        return null; // no message
    }

    void sendMessage(Message targetMessage) {
        OSPrinter.printf("Scheduler{%s}: sendMessage\n", runningPCB);

        // find target
        PCB target = processPIDs.get(targetMessage.targetPID);
        if(target == null) {
            OSPrinter.println("Error in scheduler.sendMessage: No process with that PID exists");
            return;
        }

        /* Send message and add process to running queue */
        target.getMessageQueue().add(targetMessage);
        if(target.isWaiting()) {
            getPriorityQueue(target).add(target);
            target.isWaiting(false);
        }
    }

    /** Wake sleeping processes when  */
    private void wakeProcess() {
        PCB peek;
        while ((peek = sleeping.peek()) != null)
        {
            long currentTime = clock.millis();
            if(peek.timeToWake <= currentTime) {
                /* Wake process */
                Queue<PCB> priority = getPriorityQueue(peek);
                peek.isSleeping(false);
                priority.add(sleeping.poll());

                OSPrinter.println("Waking process: " + peek + "time: " + currentTime);
                OSPrinter.println(peek.getPriority() + " process list: " + priority);
                OSPrinter.println("Sleeping process list: " + sleeping);
            }
            else break;
        }
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
//        throw new RuntimeException("All queues are empty");
        return null;
    }

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

    public PCB getRunningPCB() { return runningPCB; }

    public HashMap<Integer, PCB> getProcessPIDs() {
        return processPIDs;
    }
}

