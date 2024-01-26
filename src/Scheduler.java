import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class Scheduler {
    private LinkedList<UserlandProcess> processList;

    private Timer timer;
    public UserlandProcess currProcess;

    long quantum = 250;


    Scheduler()
    {
        processList = new LinkedList<>();
        processList.add();
        currProcess = ;

        timer = new Timer("Interrupt");

        timer.schedule(currProcess::requestStop, quantum);
    }
    public int createProcess(UserlandProcess up)
    {
        processList.add(up);
        if(currProcess.isDone())
            switchProcess();
    }

    public void switchProcess()
    {

    }
}
