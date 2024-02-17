public class GoodbyeWorld extends UserlandProcess {
    void main() {
        while(true){
            try {
                System.out.println("Goodbye World");
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            OS.sleep(5000);
            cooperate();
        }
    }
}
