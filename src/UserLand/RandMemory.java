package UserLand;

import KernelLand.OS;

import javax.print.attribute.standard.PageRanges;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import static KernelLand.Kernel.PAGE_COUNT;
import static KernelLand.Kernel.PAGE_SIZE;

public class RandMemory extends UserlandProcess {
    Scanner scanner = new Scanner(System.in);

    @Override
    void main() {
        int page_number;
        byte[] read;

        for(int i = 0 ; i < 2; i++) {
            page_number = allocate_page();

            System.out.println("Writing to page: " + page_number);

            fillPage(page_number);
            read = read_page(page_number);

            System.out.println(Arrays.toString(read));
        }

//        boolean running = true;
//        while(running) {
//            System.out.print("allocate a page and fill with random bytes(y/n): ");
//            String input = scanner.next();
//            if(input.equalsIgnoreCase("y")) {
//                page_number = allocate_page();
//
//                System.out.println("Writing to page: " + page_number);
//
//                fillPage(page_number);
//                read = read_page(page_number);
//
//                System.out.println(Arrays.toString(read));
//
//                cooperate();
//            }
//            else running = false;
//        }
    }

    private int allocate_page() {
        int virtual_address = OS.allocateMemory(PAGE_SIZE);
        if(virtual_address < 0)
            throw new RuntimeException("alloc failed");

        return virtual_address / PAGE_SIZE;
    }

    private void fillPageRandom(int pageNumber) {
        Random rand = new Random();

        byte[] random = new byte[PAGE_SIZE];
        rand.nextBytes(random);

        write_memory(pageNumber * PAGE_SIZE, random);
    }

    /**
     * fills page with numbers
     * @param pageNumber
     */
    private void fillPage(int pageNumber) {
        byte[] arr = new byte[PAGE_SIZE];
        for(byte i = 0; i < arr.length; i++)
            arr[i] = i;
        write_memory(pageNumber * PAGE_SIZE , arr);
    }

    private byte[] read_page(int page_number) {
        int page_address = page_number * PAGE_SIZE;
        return read_memory(page_address, PAGE_SIZE);
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
