public class Main {

    public static void main(String[] args)
    {
        /* CHECK OSDebug.txt FOR MORE INFORMATION */

        OS.startup(new HelloWorld());
        OS.createProcess(new Calculate(Calculate.Equation.FIBBONACI));
    }
}
