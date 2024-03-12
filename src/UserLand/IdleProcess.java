package UserLand;

public class IdleProcess extends UserlandProcess{
    @Override
    void main() {
        while(true){
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            cooperate();
        }
    }
}
