package Devices;

import java.util.Arrays;
import java.util.Random;

public class RandomDevice implements Device {
    static final int MAX_RANDOM = 10;

    public final Random[] randoms = new Random[MAX_RANDOM];

    private int i = 0;

    /**
     * Opens a new Random device and returns device ID
     *
     * @param s Seed for our new RandomDevice
     * @return id of new random device. Returns -1 if no new device can be created
     */
    @Override
    public int open(String s) {
        Random randomDevice;
        if(!s.isEmpty())
            randomDevice = new Random(Integer.parseInt(s)); // treat string as seed
        else randomDevice = new Random();

        /* Find and fill empty pos */
        int count = 0; // times the loop has run
        while(randoms[i] != null) {
            if(++i >= MAX_RANDOM)
                i = 0;
            if(++count >= 10) return -1; // no empty spot
        }
        randoms[i] = randomDevice;

        return i; // we know i is no longer empty
    }

    /**
     *
     * @param id of the {@code RandomDevice} to be closed
     */
    @Override
    public void close(int id) {
        if(id > MAX_RANDOM) throw new RuntimeException("id exceeds random device limit\n");
        if(id < 0) throw new RuntimeException("Invalid id");

        randoms[id] = null;
    }

    /**
     * Reads {@code size} random bytes, from device with input id,
     * and returns them in an array.
     *
     * @param id of the Random Device
     * @param size size to be read, in bytes
     * @return array of random bytes
     */
    @Override
    public byte[] read(int id, int size) {
        if(id > MAX_RANDOM) throw new RuntimeException("id exceeds random device limit\n");
        if(id < 0) throw new RuntimeException("Invalid id");

        byte[] arr = new byte[size];
        randoms[id].nextBytes(arr);
        return arr;
    }

    @Override
    public void seek(int id, int to) {
        if(id > MAX_RANDOM) throw new RuntimeException("id exceeds random device limit\n");
        if(id < 0) throw new RuntimeException("Invalid id");

        byte[] arr = new byte[to];
        randoms[id].nextBytes(arr);
    }

    @Override
    public int write(int id, byte[] data) {
        return 0;
    }

    public void printRandoms(){
        System.out.println(Arrays.toString(randoms));
    }
}
