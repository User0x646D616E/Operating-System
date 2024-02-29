package UserLand;

import java.util.Random;

public class Plead extends UserlandProcess {
    @Override
    void main(){
        while(true){
            System.out.println(choosePlead());
            cooperateOnInterrupt();
        }
    }

    private String choosePlead()
    {
        Random random = new Random();
        int totalWeight = 6 + 3 + 1;
        int randomNumber = random.nextInt(totalWeight) + 1;

        if (randomNumber <= 6) {
            return "please";
        } else if (randomNumber <= 9) {
            return "do Not";
        } else {
            return "Don't type into the file";
        }
    }
}
