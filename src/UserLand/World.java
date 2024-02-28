package UserLand;

import KernelLand.OS;

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
            System.out.flush();
            cooperate();
        }
    }

    @Override
    public void setPriority(OS.Priority priority){ // TODO for testing
//        str = priority.name();
    }
}
