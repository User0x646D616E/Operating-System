package KernelLand;

public class Message {
    int senderPID;
    int targetPID;


    public enum MessageDef{
        STRING,
    }
    /** Definition of the type of message being sent
     * Reciver of message will decode this to see how {@code data} should be handled */
    MessageDef messageDef;

    byte[] data;

    /** Constructor to create a message that sends a string */
    public Message(int targetPID, String message) {
        this.targetPID = targetPID;
        this.messageDef = MessageDef.STRING;
        this.data = message.getBytes();
    }

    /** Copy constructor */
    public Message(Message message) {
        this.senderPID = message.senderPID;
        this.targetPID = message.targetPID;
        this.messageDef = message.messageDef;
        this.data = message.data;
    }

    public MessageDef getMessageDef() {
        return messageDef;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return String.format("""
                Sender pid: %d
                Target pid: %d
                Message:    %s
                """, senderPID, targetPID, messageDef);
    }
}
