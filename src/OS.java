import java.util.ArrayList;

public class OS {
    private static Kernal kernal;

    enum CallType {
        CREATEPROCESS,
        SWITCHPROCESS,
    }
    static CallType currentCall;

    static ArrayList<Object> params;
    static Object returnValue;


    public static int CreateProcess(UserlandProcess up)
    {
        params = new ArrayList<>();
        currentCall = CallType.CREATEPROCESS;

        //@TODO
        //switch to kernal
        //return (int)
    }

    public static void switchProcess() {
    }

    public static void Startup(UserlandProcess init)
    {
        kernal = new Kernal();
        IdleProcess idle = new IdleProcess();

        CreateProcess(init);
        CreateProcess(idle);
    }
}
