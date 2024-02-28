package Devices;

import java.util.HashMap;
import java.util.Map;

public class VFS implements Device {
    Map<Integer, Device> deviceMap = new HashMap<>();


    @Override
    public int open(String s) {
        String[] words = s.split(" ");

        String deviceName = words[0];
        switch (deviceName){
            case "random" -> deviceMap.get(0).open(words[1]);
            case "file"   -> deviceMap.get(1).open(words[1]);
        }
        return 0;
    }

    @Override
    public void close(int id) {

    }

    @Override
    public byte[] read(int id, int size) {
        return new byte[0];
    }

    @Override
    public void seek(int id, int to) {

    }

    @Override
    public int write(int id, byte[] data) {
        return 0;
    }

    private String parseDeviceName() {
        return null;
    }
}
