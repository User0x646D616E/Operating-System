package UserLand;

import KernelLand.Message;
import KernelLand.Message.*;

import static KernelLand.OS.*;

public class Ping extends UserlandProcess {
    @Override
    void main() {
        while(true) {
            Message received;

//            sendMessage(new Message(getPidByName("Pong"), "ping"));
            sendMessage(new Message(getPidByName("pong"), "ping"));

            received = waitForMessage();
            MessageDef messageDef = received.getMessageDef();

            String string = null;
            switch(messageDef) {
                case STRING -> string = new String(received.getData());

            }
            if(string != null)
                System.out.println("ping received message: " + string);

            cooperateOnInterrupt();
        }
    }
}
