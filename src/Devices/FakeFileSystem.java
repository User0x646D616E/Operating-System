package Devices;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class FakeFileSystem implements Device {
    static final int MAX_FILES = 10;

    private int i = 0;
    private final RandomAccessFile[] randomAccessFiles = new RandomAccessFile[10];


    /**
     * Opens file with file path {@code s}
     * @param s the file path
     * @return id of a file. Returns -1 if no file can be created
     */
    @Override
    public int open(String s) {
        if(s == null || s.isEmpty())
            throw new RuntimeException("File path is empty or null");


        /* Find and fill empty pos */
        int count = 0; // times the loop has run
        while(randomAccessFiles[i] != null) {
            if(++i >= MAX_FILES)
                i = 0;
            if(++count >= 10) return -1; // no empty spot
        }

        try {
            randomAccessFiles[i] = new RandomAccessFile(s, "rw");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        return i;
    }

    /**
     * Close file with ID id
     * @param id of the file to be closed
     */
    @Override
    public void close(int id) {
        try {
            randomAccessFiles[id].close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        randomAccessFiles[id] = null;
    }

    /**
     * Read an array of bytes from the file with ID id.
     *
     * @param id the id of the file
     * @param size number of bytes to be read from the file
     * @return read The array of bytes read from the file, terminated by -1
     */
    @Override
    public byte[] read(int id, int size) {
        byte[] read = new byte[size];
        try
        {
            for(int i = 0; i < size; i++){
                read[i] = (byte) randomAccessFiles[id].read();
                if(read[i] == -1) break; // end of file
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return read;
    }

    @Override
    public void seek(int id, int to) {

    }

    /**
     * Write data to a file with ID id.
     * @param id the id if the file to be written to.
     * @param data the byte[] to be written
     * @return TODO not sure yet
     */
    @Override
    public int write(int id, byte[] data) {
        if(randomAccessFiles[id] == null)
            throw new RuntimeException("404: file not found");

        try {
            randomAccessFiles[id].write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    public void printFiles() {
        System.out.println(Arrays.toString(randomAccessFiles));
    }
}
