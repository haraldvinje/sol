package game;


import engine.*;

import engine.audio.AudioMaster;
import engine.character.*;
import engine.combat.DamageResolutionSys;
import engine.combat.DamageableComp;
import engine.combat.DamagerComp;
import engine.combat.abilities.Ability;
import engine.combat.abilities.AbilityComp;
import engine.combat.abilities.AbilitySys;
import engine.combat.abilities.MeleeAbility;
import engine.graphics.*;
import engine.graphics.text.Font;
import engine.graphics.text.FontType;
import engine.physics.*;
import engine.window.Window;
import utils.maths.M;

import java.util.ArrayList;
import java.util.List;

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


    public void init() {
        window = new Window(0.8f, 0.8f,"SIIII");
        userInput = new UserInput(window, GameUtils.VIEW_WIDTH, GameUtils.VIEW_HEIGHT);

        Font.loadFonts(FontType.BROADWAY);
        AudioMaster.init();


        wc = new WorldContainer(GameUtils.VIEW_WIDTH, GameUtils.VIEW_HEIGHT);


        System.out.println("HEELLLLLOOOOO");


        GameUtils.PROGRAM = GameUtils.OFFLINE;

        GameUtils.assignComponentTypes(wc);
        GameUtils.assignSystems(wc, window, userInput);

        //GameUtils.createInitialEntities(wc);
        GameUtils.createMap(wc);

        List<Integer> team1Chars = new ArrayList<>();
        List<Integer> team2Chars = new ArrayList<>();

        team1Chars.add(CharacterUtils.SHRANK);
//        team1Chars.add(CharacterUtils.SHRANK);

        team2Chars.add(CharacterUtils.SCHMATHIAS);

        CharacterUtils.createOfflineCharacters(wc, team1Chars, team2Chars, 0, 0);
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
