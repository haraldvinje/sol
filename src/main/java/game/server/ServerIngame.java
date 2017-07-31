package game.server;

import engine.UserInput;
import engine.WorldContainer;
import engine.audio.AudioMaster;
import engine.character.*;
import engine.graphics.text.Font;
import engine.graphics.text.FontType;
import engine.graphics.view_.View;
import engine.physics.*;
import engine.window.Window;
import game.CharacterUtils;
import game.GameUtils;
import game.SysUtils;

import java.util.*;


/**
 * Created by eirik on 13.06.2017.
 */
public class ServerIngame {


    private static final float FRAME_INTERVAL = 1.0f/60.0f;


    public static final float WINDOW_WIDTH = GameUtils.MAP_WIDTH /4,
                                WINDOW_HEIGHT = GameUtils.MAP_HEIGHT /4;


//    private ServerCharacterSelection charactersSelected;

    private ServerGame serverGame;

    private Window window;
    private UserInput userInput;

    private WorldContainer wc;

    private boolean running = true;
    private long lastTime;


    private ServerGameTeams teams;
//    private List< List<ServerClientHandler> > teamClients;
//    private HashMap<ServerClientHandler, Integer> clientCharacters;

    private int[] stockLossCount;



    public ServerIngame() {

    }


    public void init( ServerGame serverGame, ServerGameTeams teams) {
        this.serverGame = serverGame;
        this.teams = teams;


        //add an stockLoss entry for every client
        stockLossCount = new int[teams.getTotalClientCount()];
    }


    public void start() {

        this.window = new Window(0.3f, "Server ingame");
        this.userInput = new UserInput(window, 1, 1);

        wc = new WorldContainer(new View(GameUtils.VIEW_WIDTH, GameUtils.VIEW_HEIGHT) );

        //load other stuff
        Font.loadFonts(FontType.BROADWAY);
        AudioMaster.init();


        GameUtils.assignComponentTypes(wc);
        SysUtils.addServerSystems(wc, window, Arrays.asList( teams.getAllClients() ) );



        System.out.println("Server game initiated with clients: "+ Arrays.toString(teams.getAllClients()) );


        //create entities
        GameUtils.createLargeMap(wc);
        CharacterUtils.createServerCharacters(wc, teams);


        //game loop
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
        serverGame.setShouldTerminate();
    }

    private void onTerminate() {
        wc.terminate();

        window.close();
    }


    public ServerGameTeams getTeams() {
        return teams;
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

