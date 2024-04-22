import KernelLand.OS;
import UserLand.*;


import static KernelLand.Kernel.PAGE_SIZE;
import static KernelLand.OS.*;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        startup(new IdleProcess());

        OS.createProcess(new World("Fuck me you little cum slut"));
        OS.createProcess(new World("Massive fart"));
        OS.createProcess(new Plead());


    }
}
