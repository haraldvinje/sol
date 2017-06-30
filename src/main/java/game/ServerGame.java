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
import engine.network.server.ServerClientHandler;
import engine.network.server.ServerNetworkSys;
import engine.physics.*;
import engine.window.Window;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by eirik on 13.06.2017.
 */
public class ServerGame implements Runnable{


    private static final float FRAME_INTERVAL = 1.0f/60.0f;


    public static final float WINDOW_WIDTH = GameUtils.MAP_WIDTH /4,
                                WINDOW_HEIGHT = GameUtils.MAP_HEIGHT /4;


    private boolean shouldTerminate = false;


    private Window window;
    private UserInput userInput;

    private WorldContainer wc;


    private boolean running = true;

    private long lastTime;

    private int[] stockLossCount;


    public void init( List<ServerClientHandler> clientHandlers) {

        //window = new Window(WINDOW_WIDTH, WINDOW_HEIGHT, "Server   SIIII");
        //userInput = new UserInput(window);

        //add an stockLoss entry for every client
        stockLossCount = new int[clientHandlers.size()];

        GameUtils.CLIENT_HANDELERS = clientHandlers;

        GameUtils.PROGRAM = GameUtils.SERVER;

        wc = new WorldContainer();

    }

    @Override
    public void run() {

        this.window = new Window(WINDOW_WIDTH, WINDOW_HEIGHT, "Server ingame");
        this.userInput = new UserInput(window);

        System.out.println("Server game initiated with clients: "+GameUtils.CLIENT_HANDELERS);

        //create entities
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

        onTerminate();
    }




    public void update() {

        window.pollEvents();

        wc.updateSystems();

        //check stocks left
        Integer charNumb = 0;
        for (int entity : wc.getEntitiesWithComponentType(CharacterComp.class) ) {
            AffectedByHoleComp affholeComp = (AffectedByHoleComp) wc.getComponent(entity, AffectedByHoleComp.class);
            if (affholeComp.isHoleAffectedFlag()) {
                stockLossCount[charNumb]++;
            }

            if (stockLossCount[charNumb] >= 3) {
                gameOver(1-charNumb);
            }

            charNumb++;
        }

    }
    private void gameOver(int winner) {
        System.out.println("Player "+ winner + " won!");
        setShouldTerminate();

    }

    private void onTerminate() {
        wc.terminate();

        window.close();
    }

    private void setShouldTerminate() {
        synchronized (this) {
            shouldTerminate = true;
        }
    }
    public boolean isShouldTerminate(){
        synchronized (this) {
            return shouldTerminate;
        }
    }

    public void terminate() {
        running = false;
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

