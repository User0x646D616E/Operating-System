package Devices.UnitTests;

import Devices.RandomDevice;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class UnitTest_RandomDevice {
    RandomDevice randomDevice = new RandomDevice();

    @Test
    public void testOpen() {
        // Test case 1: open max random
        for(int i = 0; i<=10; i++)
            randomDevice.open("");
        assertEquals(-1, randomDevice.open(""));
    }

    @Test
    public void testClose() {
        for(int i = 0; i<10; i++){
            randomDevice.open("");
        }
        randomDevice.printRandoms();
        for(int i = 0; i<10; i++){
            randomDevice.close(i);
        }
        randomDevice.printRandoms();
    }

    @Test
    public void testRead() {
        int id = randomDevice.open("");
        byte[] arr = randomDevice.read(id, 10);
        System.out.print(Arrays.toString(arr));
    }
}
