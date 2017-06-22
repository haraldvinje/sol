package game;

/**
 * Created by eirik on 22.06.2017.
 */
public class MainServer {

    public static void main(String[] args) {

        ServerGame sg = new ServerGame();
        sg.init();
        sg.start();
    }
}
