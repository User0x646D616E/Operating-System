package UserLand;

import KernelLand.Message;

import static KernelLand.OS.sendMessage;
import static KernelLand.OS.waitForMessage;
import static KernelLand.OS.getPidByName;

public class Pong extends UserlandProcess {

    @Override
    void main() {
        while(true) {
            Message received;

            received = waitForMessage();
            Message.MessageDef messageDef = received.getMessageDef();

            String string = null;
            switch(messageDef) {
                case STRING -> string = new String(received.getData());

            }
            if(string != null)
                System.out.println("pong received message: " + string);

//        sendMessage(new Message(getPidByName("Ping"), "pong"));
            sendMessage(new Message(getPidByName("ping"), "pong"));

            cooperateOnInterrupt();
        }
    }
}
