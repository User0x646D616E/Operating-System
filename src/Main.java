import KernelLand.OS;
import UserLand.*;

import static KernelLand.OS.*;

public class Main {
    public static void main(String[] args) {
        startup(new HelloWorld());
        OS.createProcess(new Pong());
        OS.createProcess(new Ping());
        OS.createProcess(new GoodbyeWorld());
    }
}
