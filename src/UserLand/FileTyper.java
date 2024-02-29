package UserLand;

import Devices.FakeFileSystem;

import java.util.Scanner;

import static KernelLand.OS.*;


public class FileTyper extends UserlandProcess {
    @Override
    void main() {
        Scanner scanner = new Scanner(System.in);

        int id= open("file FileTyper.txt");
        byte[] buffer;

        while(true)
        {
            System.out.print("Type into the file: ");
            String inputString = scanner.nextLine();
            
            buffer = inputString.getBytes();
            write(id, buffer);
            write(id, new byte[]{'\n'});

            sleep(1000, id);
            cooperate();
        }
    }
}
