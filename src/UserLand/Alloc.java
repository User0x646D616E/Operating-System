package UserLand;

import KernelLand.OS;

import java.util.Locale;
import java.util.Scanner;

public class Alloc extends UserlandProcess {
    @Override
    void main() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nThis program allocates a page and allows you to type to memory\n");
        int curr_addr = allocate_page();

        boolean running = true;
        while(running) {
            byte[] read;

            System.out.print("Type string: ");
            String input = scanner.next();

            write_memory(curr_addr, input.getBytes());
            curr_addr += input.length();

            read = read_page();
            System.out.println("\n" + new String(read));

            cooperate();
        }
    }

    private int allocate_page() {
        int start_addr = OS.allocateMemory(1024);

        if(start_addr == -1)
            throw new RuntimeException("alloc failed");
        return start_addr;
    }

    private byte[] read_page() {
        return read_memory(0, 1024);
    }

    private void write_memory(int start_addr, byte[] input) {
        for(int i = 0; i < input.length; i++)
            write(start_addr+i, input[i]);
    }

    private byte[] read_memory(int start_addr, int len) {
        byte[] ret = new byte[len];

        for(int i = 0; i < len; i++)
            ret[i] = read(start_addr+i);
        return ret;
    }
}
