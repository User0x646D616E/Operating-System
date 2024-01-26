public class IdleProcess extends UserlandProcess{
    @Override
    void main()
    {
        while(true){
            cooperate();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
