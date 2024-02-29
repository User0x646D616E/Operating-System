import KernelLand.OS;
import UserLand.*;
import Utility.RealTimeFileReader;

import static KernelLand.OS.Priority.*;

public class Main {

    public static void main(String[] args)
    {
        /* CHECK OSDebug.txt FOR MORE INFORMATION */
//        RealTimeFileReader.start();

        OS.startup(new FileTyper());
        OS.createProcess(new Plead());
    }
}
