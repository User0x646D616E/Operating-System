package UserLand;

import KernelLand.OS;

public class GoodbyeWorld extends UserlandProcess {
    void main() {
        while(true){
            try {
                System.out.println("Goodbye World");
                Thread.sleep(250);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            cooperate();
        }
    }
}
