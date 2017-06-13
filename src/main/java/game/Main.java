package game;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello");

        Game g = new Game();
        g.init();
        g.start();
    }

    public String toString () {
        return "main";
    }
}
