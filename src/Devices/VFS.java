package Devices;

import java.util.HashMap;
import java.util.Map;

public class VFS implements Device {
    int new_vfsID = 0;
    /** TODO Maps our vfs ids to a device and id pair. The device id pair represents a device with its own id. */
    static Map<Integer, Map.Entry<Integer, Device>> vfsDevices = new HashMap<>();

    static private final Map<String, Device> deviceMap = new HashMap<>();
    static {
        deviceMap.put("random", new RandomDevice());
        deviceMap.put("file", new FakeFileSystem());
    }


    @Override
    public int open(String s) {
        String[] words = s.split(" ");

        /* Find and call open on Device */
        Device device = deviceMap.get(words[0]);
        String param = words[1];

        vfsDevices.put(new_vfsID, Map.entry(device.open(param), device));

        return new_vfsID++;
    }

    /**
     *
     * @param id the vfs id
     */
    @Override
    public void close(int id) {
        Device device = vfsDevices.get(id).getValue();
        int deviceID = vfsDevices.get(id).getKey();

        device.close(deviceID);
    }

    @Override
    public byte[] read(int id, int size) {
        Device device = vfsDevices.get(id).getValue();
        int deviceID = vfsDevices.get(id).getKey();

        return device.read(deviceID, size);
    }

    @Override
    public void seek(int id, int to) {
        Device device = vfsDevices.get(id).getValue();
        int deviceID = vfsDevices.get(id).getKey();

        device.seek(deviceID, to);
    }

    @Override
    public int write(int id, byte[] data) {
        Device device = vfsDevices.get(id).getValue();
        int deviceID = vfsDevices.get(id).getKey();

        return device.write(deviceID, data);
    }
}


/*
    private Device queryDevice(String s) {
        String[] words = s.split(" ");

        String deviceName = words[0];
        String param = words[1];

        Map.Entry<Device, int[]> deviceID_Pair;
        int deviceID;
        switch (deviceName) {
            case "random" -> deviceID_Pair = vfsDevices.get(0);
            case "file"   -> deviceID_Pair = vfsDevices.get(1);
            default -> throw new RuntimeException("Device not found\n");
        }
    }
*/
