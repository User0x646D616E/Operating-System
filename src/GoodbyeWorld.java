public class GoodbyeWorld extends UserlandProcess {
    void main() {
        while(true){
            try {
                System.out.println("Goodbye World");
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            OS.sleep(3000, this.pid); // we need to interrupt the thread
            cooperate();
        }
    }
}
