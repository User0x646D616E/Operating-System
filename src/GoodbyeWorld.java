public class GoodbyeWorld extends UserlandProcess {
    void main() {
        while(true){
            try {
                System.out.println("Goodbye World");
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            cooperate();
        }
    }

    void function(String str){
        try {
            System.out.println(str);
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        cooperate();
    }
}
