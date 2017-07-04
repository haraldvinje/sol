package engine.network.client.clientStates;

import engine.PositionComp;
import engine.WorldContainer;
import engine.graphics.ColoredMeshComp;
import engine.graphics.ColoredMeshUtils;
import engine.network.client.Client;
import engine.network.client.ClientState;
import game.ClientGame;

/**
 * Created by eirik on 04.07.2017.
 */
public class ClientIngameState extends ClientState {


    private Thread gameThread;
    private ClientGame game;


    @Override
    public void init() {
        super.init();

        createInitialEntities(wc);
    }

    @Override
    public void onEnter() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        createGame();
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onExit() {
        terminateGame();
    }

    private void createGame() {
        game = new ClientGame(client.getSocket());
        game.init();
        gameThread = new Thread(game);

        gameThread.start();
    }
    private void terminateGame() {
        game.terminate();

        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void createInitialEntities(WorldContainer wc) {
        int circ = wc.createEntity();
        wc.addComponent(circ, new PositionComp(Client.WINDOW_WIDTH/2, Client.WINDOW_HEIGHT/2));
        wc.addComponent(circ, new ColoredMeshComp(ColoredMeshUtils.createCircleTwocolor(Client.WINDOW_HEIGHT*(3/4), 32)));
    }
}
