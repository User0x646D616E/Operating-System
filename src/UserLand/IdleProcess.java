package UserLand;

public class IdleProcess extends UserlandProcess{
    @Override
    void main() {
        while(true){
            try {
                Thread.sleep(25);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            cooperate();
        }
    }
}
