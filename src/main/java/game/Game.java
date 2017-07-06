package game;


import engine.*;

import engine.character.*;
import engine.combat.DamageResolutionSys;
import engine.combat.DamageableComp;
import engine.combat.DamagerComp;
import engine.combat.abilities.Ability;
import engine.combat.abilities.AbilityComp;
import engine.combat.abilities.AbilitySys;
import engine.combat.abilities.MeleeAbility;
import engine.graphics.*;
import engine.physics.*;
import engine.window.Window;
import utils.maths.M;

/**
 * Created by eirik on 13.06.2017.
 */
public class Game {


    private static final float FRAME_INTERVAL = 1.0f/60.0f;

    public static final float WINDOW_WIDTH = 1600f, WINDOW_HEIGHT = 900f;


    private Window window;
    private UserInput userInput;


    private ColoredMesh vao;

    private long lastTime;

    private WorldContainer wc;

    //private CollisionDetectionSys cds;


    private int player;
    private int sandbag;
    private int hole;




    public void init() {
        window = new Window("SIIII");
        userInput = new UserInput(window, GameUtils.MAP_WIDTH, GameUtils.MAP_HEIGHT);

        wc = new WorldContainer();

        //cds = new CollisionDetectionSys(wc);

        System.out.println("HEELLLLLOOOOO");


        GameUtils.PROGRAM = GameUtils.OFFLINE;

        GameUtils.assignComponentTypes(wc);
        GameUtils.assignSystems(wc, window, userInput);

        GameUtils.createInitialEntities(wc);

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

        window.close();

    }



    public void update() {
        window.pollEvents();

        wc.updateSystems();

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
