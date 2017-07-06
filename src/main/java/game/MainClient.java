package game;

import engine.network.client.Client;

/**
 * Created by eirik on 22.06.2017.
 */
public class MainClient {

    public static void main(String[] args) {

        if (args.length != 1) {
            System.err.println("Give the server host name as input when executing");
            return;
        }

//        GameUtils.HOST_NAME = args[0];
//        ClientGame cg = new ClientGame();
//        cg.init();
//        cg.start();

        Client client = new Client(args[0]);
        client.init();
        client.start();
    }
}
