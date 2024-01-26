public class HelloWorld extends UserlandProcess {
    @Override
    void main() {
        HelloWorld hello = new HelloWorld();

        while(true){
            System.out.println("Hello World");
            cooperate();
        }
    }
}
