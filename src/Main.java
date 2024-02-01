public class Main {

    public static void main(String[] args) throws InterruptedException {
        OS.startup(new HelloWorld());
        OS.createProcess(new GoodbyeWorld());
    }
}
