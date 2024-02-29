package Utility;

import java.io.*;

public class OSPrinter {

    static private final FileOutputStream fileOutputStream;
    static private PrintStream printStream;

    static{
        String filePath = "OSDebug.txt";
        try{
            fileOutputStream = new FileOutputStream(new File(filePath));
            printStream = new PrintStream(fileOutputStream);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void print(String str){
        printStream.print(str);
    }
    public static void println(String str){
        printStream.println(str);
    }
    public static void printf(String str, Object... args){
        printStream.printf(str, args);
    }
}
