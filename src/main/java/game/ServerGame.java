package game;

import engine.PositionComp;
import engine.RotationComp;
import engine.UserInput;
import engine.WorldContainer;
import engine.character.*;
import engine.combat.DamageResolutionSys;
import engine.combat.DamageableComp;
import engine.combat.DamagerComp;
import engine.graphics.*;
import engine.network.server.ServerNetworkSys;
import engine.physics.*;
import engine.window.Window;



/**
 * Created by eirik on 13.06.2017.
 */
public class ServerGame {


    private static final float FRAME_INTERVAL = 1.0f/60.0f;


    public static final float WINDOW_WIDTH = GameUtils.MAP_WIDTH /4,
                                WINDOW_HEIGHT = GameUtils.MAP_HEIGHT /4;


    private Window window;
    private UserInput userInput;

    private WorldContainer wc;



    private long lastTime;



    public void init() {
        window = new Window(WINDOW_WIDTH, WINDOW_HEIGHT, "Server   SIIII");
        userInput = new UserInput(window);

        wc = new WorldContainer();

        System.out.println("HEELLLLLOOOOO");


        //create entities
        GameUtils.PROGRAM = GameUtils.SERVER;

        GameUtils.assignComponentTypes(wc);

        GameUtils.assignSystems(wc, window, userInput);


        GameUtils.createInitialEntities(wc);


//        players= new int[2];
//
//        players[0] = GameUtils.createPlayer(wc);
//        players[1] = GameUtils.createPlayer(wc);
//
//        //sandbag = createSandbag(wc);
//
//        float wallThickness = 64f;
//        createWall(wc, wallThickness/2, WINDOW_HEIGHT/2, wallThickness, WINDOW_HEIGHT);
//        createWall(wc, WINDOW_WIDTH-wallThickness/2, WINDOW_HEIGHT/2, wallThickness, WINDOW_HEIGHT);
//
//        createWall(wc, WINDOW_WIDTH/2, wallThickness/2, WINDOW_WIDTH-wallThickness*2, wallThickness);
//        createWall(wc, WINDOW_WIDTH/2, WINDOW_HEIGHT-wallThickness/2, WINDOW_WIDTH-wallThickness*2, wallThickness);
//
//
//        hole = createHole(wc);

//        createBackground(wc);

    }


    /**
     * blocking while the game runs
     */
    public void start() {
        lastTime = System.nanoTime();

        float timeSinceUpdate = 0;

        while (true) {
            timeSinceUpdate += timePassed();
            //System.out.println("Time since update: "+timeSinceUpdate);

            if (timeSinceUpdate >= FRAME_INTERVAL) {
                timeSinceUpdate -= FRAME_INTERVAL;

                update();
            }


            if (window.shouldClosed() || userInput.isKeyboardPressed(UserInput.KEY_ESCAPE))
                break;
        }

        terminate();

    }




    public void update() {

        window.pollEvents();

        wc.updateSystems();

    }

    private void terminate() {
        wc.terminate();

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

