public class Calculate extends UserlandProcess{
    enum Equation{
        NEXTPRIME,
        FIBBONACI,
        ADD,
    }
    Equation equation;

    Calculate(Equation whatEquation){
       equation = whatEquation;
    }
     void main() {
        switch (equation) {
            case NEXTPRIME -> nextPrime();
            case FIBBONACI -> fibbonaci(1000);
        }
    }

    public void fibbonaci(int n) {
        long a = 0, b = 1;
        System.out.println("Fibonacci sequence up to " + n + " terms:");
        for (int i = 0; i < n; i++) {
            long next = a + b;
            System.out.print("Next fibonacci " + next + "\n");
            a = b;
            b = next;

            halt();
        }
    }

    public void nextPrime(){
        int nextPrime;

        int currentNumber = 2; // Start with the first prime number
        while(true){
            nextPrime = findNextPrime(currentNumber);
            System.out.println("next prime: " + nextPrime);
            currentNumber = nextPrime;

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            halt();
        }
    }

    // Function to find the next prime number after a given number
    public int findNextPrime(int n) {
        while (true) {
            n++; // Check the next number
            if (isPrime(n)) {
                return n; // Return if the number is prime
            }
        }
    }

    // Function to check if a number is prime
    public boolean isPrime(int n) {
        if (n <= 1) {
            return false;
        }
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) {
                return false; // If the number is divisible by any other number, it's not prime
            }
        }
        return true; // If the number is not divisible by any other number, it's prime
    }

    @Override
    public String toString() {
        return equation.toString();
    }
}
