package UserLand;

import java.util.*;

import static KernelLand.OS.*;


public class FileTyper extends UserlandProcess {
    Scanner scanner = new Scanner(System.in);
    final Map<Integer, String> fileIDs = new HashMap<>();

    int id;
    Object input;

    @Override
    void main() {
        promptUser();

        while(true) {
            input = getString("Type into file: ");
            write(id, ((String)input).getBytes());

            sleep(1000, 0);
            cooperate();
        }
    }

    private boolean promptUser() {
        while(true) {
            System.out.printf("""
            
            What would you like to do:
            File IDs: %s
            1) Open a file
            2) Close a file
            3) type into the file
            4) Read a file
            """
            , fileIDs);

            switch(getInt("Type {number}: ")) {
                case 1 -> {
                    input = getString("Type file name: ");
                    fileIDs.put(open("file " + input), (String)input);
                }
                case 2 -> {
                    System.out.print("Type file id: "); id = scanner.nextInt();
                    close(id);
                    fileIDs.remove(id);
                    System.out.println("File closed");
                }
                case 3 -> {
                    id = getInt("Type file id: ");
                    createProcess(new Plead());
                    return true;
                }
                case 4 -> {
                    System.out.print("Type file id: "); id = scanner.nextInt();
                    System.out.print(Arrays.toString(read(id, 50)));
                }
            }
            cooperate();
        }
    }

    private int getInt(String prompt) {
        while(true) {
            System.out.print(prompt);
            try {
                input = scanner.nextInt();
                break;
            } catch(InputMismatchException e) {
                scanner.nextLine(); // eat invalid input
                System.out.println("Invalid input please enter an Int");
            }
        }
        scanner.nextLine(); // eat new line
        return (int)input;
    }

    private String getString(String prompt) {
        String returnStr;

        while(true) {
            System.out.print(prompt);
            try {
                returnStr = scanner.nextLine();
                break;
            } catch(InputMismatchException e) {
                scanner.nextLine(); // eat invalid input
                System.out.println("Invalid input please enter a string");
            }
        }
        return returnStr;
    }
}
