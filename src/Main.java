import KernelLand.OS;
import UserLand.FileTyper;
import UserLand.HelloWorld;

import java.io.File;

public class Main {

    public static void main(String[] args)
    {
        /* CHECK OSDebug.txt FOR MORE INFORMATION */

        OS.startup(new HelloWorld());
        OS.createProcess(new FileTyper());
    }
}
