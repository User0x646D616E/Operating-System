public class Main {

    public static void main(String[] args)
    {
        /* CHECK OSDebug.txt FOR MORE INFORMATION */

        OS.startup(new HelloWorld());
        OS.createProcess(new World("Real time"), OS.Priority.REALTIME);
        OS.createProcess(new World("Real time"), OS.Priority.REALTIME);
        OS.createProcess(new World("Real time"), OS.Priority.REALTIME);
        OS.createProcess(new World("Interactive"), OS.Priority.INTERACTIVE);
        OS.createProcess(new World("Interactive"), OS.Priority.INTERACTIVE);
        OS.createProcess(new World("Interactive"), OS.Priority.INTERACTIVE);
        OS.createProcess(new World("Background"), OS.Priority.BACKGROUND);
        OS.createProcess(new World("Background"), OS.Priority.BACKGROUND);
        OS.createProcess(new World("Background"), OS.Priority.BACKGROUND);
    }
}
