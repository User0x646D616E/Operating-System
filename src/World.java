public class World extends UserlandProcess {
    String str;

    World(String str){
        this.str = str;
    }
    void main(){
        while(true){
            try {
                System.out.println(str);
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            cooperate();
        }
    }
}
