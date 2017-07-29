package game.offline;


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
import engine.graphics.text.Font;
import engine.graphics.text.FontType;
import engine.graphics.view_.View;
import engine.physics.*;
import engine.window.Window;
import game.CharacterUtils;
import game.ClientGameTeams;
import game.GameUtils;
import game.SysUtils;
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

        wc = new WorldContainer( new View(GameUtils.VIEW_WIDTH, GameUtils.VIEW_HEIGHT) );


        //load stuff
        Font.loadFonts(FontType.BROADWAY);

        GameUtils.assignComponentTypes(wc);
        SysUtils.addOfflineSystems(wc, window, userInput);


        System.out.println("HEELLLLLOOOOO");

        GameUtils.createMap(wc);

        int[][] characterIds = {
                {CharacterUtils.SHRANK},
                {CharacterUtils.SCHMATHIS}
        };

        ClientGameTeams teams = new ClientGameTeams(characterIds, 0, 0);

        CharacterUtils.createOfflineCharacters(wc, teams);
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
