package game;

import engine.UserInput;
import engine.WorldContainer;
import engine.network.client.Client;
import engine.window.Window;

import java.net.Socket;

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



    private int[] players;
    private int sandbag;
    private int hole;

    private boolean running = true;

    public ClientGame(Socket socket) {
        this.socket = socket;
    }

    public void init() {

        wc = new WorldContainer();

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
        window = new Window("Client   SIIII");
        userInput = new UserInput(window, GameUtils.MAP_WIDTH, GameUtils.MAP_HEIGHT);

        GameUtils.assignComponentTypes(wc);
        GameUtils.assignSystems(wc, window, userInput);

        GameUtils.createInitialEntities(wc);



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
