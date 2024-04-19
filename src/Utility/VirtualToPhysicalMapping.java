package Utility;

public class VirtualToPhysicalMapping {
    public int physicalPage;
    public int diskPage;
    public int diskOffset;


    public VirtualToPhysicalMapping() {
        physicalPage = -1;
        diskPage = -1;
    }
}
