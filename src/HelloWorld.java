public class HelloWorld extends UserlandProcess {
    @Override
    void main() {
        while(true){
            try {
                System.out.println("Hello World");
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
           cooperateOnInterrupt();
        }
    }
}
