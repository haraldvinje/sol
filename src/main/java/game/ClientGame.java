package game;

import engine.UserInput;
import engine.WorldContainer;
import engine.network.NetworkUtils;
import engine.network.client.Client;
import engine.window.Window;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eirik on 22.06.2017.
 */
public class ClientGame implements Runnable{

    private static final float FRAME_INTERVAL = 1.0f/60.0f;

    public static final float WINDOW_WIDTH = 1600f, WINDOW_HEIGHT = 900f;


    private Window window;
    private UserInput userInput;

    private Socket socket;


    private long lastTime;

    private WorldContainer wc;

    private boolean running = true;



    private List<Integer> friendlyCharacters, enemyCharacters;
    private int clientCharacterId;
    private int team;



    public ClientGame() {
    }
    public ClientGame(Socket socket) {
        this.socket = socket;
    }


    public void init(DataInputStream inputStream, DataOutputStream outputStream, List<Integer> team1Characters, List<Integer> team2Characters, int team, int clientCharacterId) {

        this.friendlyCharacters = team1Characters;
        this.enemyCharacters = team2Characters;
        this.clientCharacterId = clientCharacterId;
        this.team = team;

        wc = new WorldContainer(GameUtils.VIEW_WIDTH, GameUtils.VIEW_HEIGHT);

        System.out.println("HEELLLLLOOOOO");

        //set program state
        GameUtils.PROGRAM = GameUtils.CLIENT;
        GameUtils.socket = socket;

    }

    public void terminate() {
        running = false;
    }

    /**
     * blocking while the game runs
     */
    @Override
    public void run() {

        System.out.println("Running client");
        window = new Window("Client   SIIII");
        userInput = new UserInput(window, GameUtils.VIEW_WIDTH, GameUtils.VIEW_HEIGHT);

        GameUtils.assignComponentTypes(wc);
        GameUtils.assignSystems(wc, window, userInput);

        GameUtils.createMap(wc);
        //create characters
//        ArrayList<Integer> team1Chars = new ArrayList<>();
//        ArrayList<Integer> team2Chars = new ArrayList<>();
//        team1Chars.add(0);
//        team2Chars.add(1);
        CharacterUtils.createClientCharacters(wc, friendlyCharacters, enemyCharacters, team, clientCharacterId);


        lastTime = System.nanoTime();

        float timeSinceUpdate = 0;

        while (running) {
            timeSinceUpdate += timePassed();
            //System.out.println("Time since update: "+timeSinceUpdate);

            if (timeSinceUpdate >= FRAME_INTERVAL) {
                timeSinceUpdate -= FRAME_INTERVAL;

                update();
            }


            if (window.shouldClosed() || userInput.isKeyboardPressed(UserInput.KEY_ESCAPE))
                break;
        }

        close();

    }


    public void update() {

        window.pollEvents();

        wc.updateSystems();
    }

    private void close() {

        window.close();
    }


    /**
     * time passed since last call to this method
     * @return
     */
    private float timePassed() {
        long newTime = System.nanoTime();
        int deltaTime = (int)(newTime - lastTime);
        float deltaTimeF = (float) deltaTime;

        lastTime = newTime;

        return deltaTimeF/1000000000;
    }

}
