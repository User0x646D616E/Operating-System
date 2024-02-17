public class Main {

    public static void main(String[] args)
    {
        /* CHECK OSDebug.txt FOR MORE INFORMATION */

        OS.startup(new HelloWorld());
        OS.createProcess(new GoodbyeWorld());
        OS.createProcess(new GoodbyeWorld());
        OS.createProcess(new GoodbyeWorld());
        OS.createProcess(new GoodbyeWorld());
        OS.createProcess(new GoodbyeWorld());
//        OS.createProcess(new HelloWorld());
//        OS.createProcess(new GoodbyeWorld());
    }
}
