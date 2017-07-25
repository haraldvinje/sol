package engine.network.client.clientStates;

import engine.PositionComp;
import engine.WorldContainer;
import engine.graphics.ColoredMeshComp;
import engine.graphics.ColoredMeshUtils;
import engine.network.client.Client;
import engine.network.client.ClientState;
import game.ClientGame;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

        List<Integer> friendlyCharacters = new ArrayList<>();
        List<Integer> enemyCharacters = new ArrayList<>();
        int team = 0;

        try {
             DataInputStream in = new DataInputStream(client.getSocketInputStream());

             team = in.readInt();
             friendlyCharacters.add( in.readInt() );
             enemyCharacters.add( in.readInt() );
        }

        catch (IOException e) {
            e.printStackTrace();
        }

        game = new ClientGame(client.getSocket());
        game.init(null, null, friendlyCharacters, enemyCharacters, team, 0);
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
        wc.addComponent(circ, new PositionComp(client.getWindowWidth()/2, client.getWindowHeight()/2));
        wc.addComponent(circ, new ColoredMeshComp(ColoredMeshUtils.createCircleTwocolor(client.getWindowHeight()*(3/4), 32)));
    }
}
