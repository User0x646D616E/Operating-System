public class Main {

    public static void main(String[] args) {
        /* CHECK OSDebug.txt FOR MORE INFORMATION */

        OS.startup(new Calculate(Calculate.Equation.FIBBONACI));
        OS.createProcess(new Calculate(Calculate.Equation.NEXTPRIME));
    }
}
