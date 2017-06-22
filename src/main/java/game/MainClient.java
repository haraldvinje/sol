package game;

/**
 * Created by eirik on 22.06.2017.
 */
public class MainClient {

    public static void main(String[] args) {

        ClientGame cg = new ClientGame();
        cg.init();
        cg.start();
    }
}
