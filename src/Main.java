import KernelLand.OS;
import UserLand.*;


import static KernelLand.Kernel.PAGE_SIZE;
import static KernelLand.OS.*;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        startup(new IdleProcess());

        for(int i = 0; i < 20; i++)
            OS.createProcess(new RandMemory());

        Thread.sleep(2000);

//        int i = 0;
//        byte[] memory = UserlandProcess.getMemory();
//        while(i < 2 * PAGE_SIZE)
//        {
//            if(i % PAGE_SIZE == 0 || i == 0)
//                System.out.print("\n\n page " + i/PAGE_SIZE + "\n\n");
//            System.out.print(i +":"+ memory[i] + " ");
//            i++;
//        }
    }
}
