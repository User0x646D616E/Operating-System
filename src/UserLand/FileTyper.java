package UserLand;

import Devices.FakeFileSystem;

import java.util.Scanner;

import static KernelLand.OS.*;


public class FileTyper extends UserlandProcess {
    @Override
    void main() {
        FakeFileSystem fileSystem = new FakeFileSystem();
        Scanner scanner = new Scanner(System.in);
        int id = fileSystem.open("FileTyper.txt");
        byte[] buffer;

        while(true)
        {
            System.out.print("Type into file: ");
            String inputString = scanner.nextLine();
            
            buffer = inputString.getBytes();
            fileSystem.write(id, buffer);

            sleep(1000, id);
            cooperate();
        }
    }
}
