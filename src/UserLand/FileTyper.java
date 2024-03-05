package UserLand;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static KernelLand.OS.*;


public class FileTyper extends UserlandProcess {


    @Override
    void main() {
        Scanner scanner = new Scanner(System.in);
        String inputString;

        int id;
        Object input;
        final Map<Integer, String> fileIDs = new HashMap<>();

        while(true)
        {
            System.out.printf("""
                What would you like to do:
                1) Open a file
                2) Close a file
                3) type into the file
                4) Read a file
                %s
                Type {number}:\s"""
                    , fileIDs);

            switch(scanner.nextInt()) {
                case 1 -> {
                    System.out.print("Type file name: "); scanner.nextLine(); inputString = scanner.nextLine();
                    fileIDs.put(open("file " + inputString), inputString);
                }
                case 2 -> {
                    System.out.print("Type file id: "); input = scanner.nextInt();
                    close((int)input);
                    fileIDs.remove((int)input);
                    System.out.println("File closed");
                }
                case 3 -> {
                    System.out.print("Type file id: "); id = scanner.nextInt();
                    System.out.print("Type: "); scanner.nextLine(); input = scanner.nextLine();
                    write(id, ((String)input).getBytes());
                }
                case 4 -> {
                    System.out.print("Type file id: "); id = scanner.nextInt();
                    System.out.print(Arrays.toString(read(id, 50)));
                }
            }

            cooperate();
        }
//        System.out.print("Would you like to open a file?(Y/N): ");
//        inputString = scanner.nextLine();
//
//        if(inputString.toUpperCase().charAt(0) == 'Y') {
//            System.out.print("Type the path of the file: ");
//            inputString = scanner.nextLine();
//
//            id = open("file " + inputString);
//            new World("please");
//            sleep(1000, id);
//        }
//        else {
//            createProcess(new GoodbyeWorld());
//            return;
//        }
//
//        for(int i = 0; i < 2; i++) {
//            System.out.print("type into the file: ");
//            inputString = scanner.nextLine();
//
//            write(id, inputString.getBytes());
//
//            createProcess(new Plead());
//            sleep(1000, id);
//        }


    }

    int openFile(String path) {

        return 0;
    }

    void closeFile() {

    }

    void readFile() {

    }

    void writeFile(int id) {
        String inputString = "";
        byte[] buffer;

        buffer = inputString.getBytes();
        write(id, buffer);
        write(id, new byte[]{'\n'});
    }
}
