package Devices.UnitTests;

import Devices.FakeFileSystem;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class UnitTest_FakeFileSystem {
    FakeFileSystem fakeFileSystem = new FakeFileSystem();


    @Test
    public void testOpen() {
        // Test case 1: open max files
        for(int i = 0; i<=10; i++)
            fakeFileSystem.open("fakeFileTest.txt");
        assertEquals(-1, fakeFileSystem.open("fakeFileTest.txt"));

    }

    @Test
    public void testClose() {
        for(int i = 0; i<10; i++){
            fakeFileSystem.open("fakeFileTest.txt");
        }
        fakeFileSystem.printFiles();
        for(int i = 0; i<10; i++){
            fakeFileSystem.close(i);
        }
        fakeFileSystem.printFiles();
    }

    @Test
    public void testWrite() {
        int id = fakeFileSystem.open("fakeFileTest.txt");

        fakeFileSystem.write(id, new byte[]{'a', 'b', 'c', 1});
    }

    @Test
    public void testRead() {
        int id = fakeFileSystem.open("FileTyper.txt");
        byte[] arr = fakeFileSystem.read(id, 10);
        System.out.print(new String(arr));
    }


}
