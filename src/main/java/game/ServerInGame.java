package game;

import engine.PositionComp;
import engine.RotationComp;
import engine.UserInput;
import engine.WorldContainer;
import engine.audio.AudioMaster;
import engine.character.*;
import engine.combat.DamageResolutionSys;
import engine.combat.DamageableComp;
import engine.combat.DamagerComp;
import engine.graphics.*;
import engine.graphics.text.Font;
import engine.graphics.text.FontType;
import engine.network.client.Client;
import engine.network.client.ClientStateUtils;
import engine.network.server.ServerClientHandler;
import engine.network.server.ServerNetworkSys;
import engine.physics.*;
import engine.window.Window;

import java.nio.IntBuffer;
import java.util.*;


/**
 * Created by eirik on 13.06.2017.
 */
public class ServerInGame {


    private static final float FRAME_INTERVAL = 1.0f/60.0f;


    public static final float WINDOW_WIDTH = GameUtils.MAP_WIDTH /4,
                                WINDOW_HEIGHT = GameUtils.MAP_HEIGHT /4;


    private boolean shouldTerminate = false;

    private ServerCharacterSelection charactersSelected;


    private Window window;
    private UserInput userInput;

    private WorldContainer wc;


    private List<ServerClientHandler> clientHandlers;


    private boolean running = true;

    private long lastTime;

    private int[] stockLossCount;

    public ServerInGame(ServerCharacterSelection characterSelection) {
        this.charactersSelected = characterSelection;
    }


    public void init( List<ServerClientHandler> clientHandlers) {

        //window = new Window(WINDOW_WIDTH, WINDOW_HEIGHT, "Server   SIIII");
        //userInput = new UserInput(window);
        this.clientHandlers = clientHandlers;
        //add an stockLoss entry for every client
        stockLossCount = new int[clientHandlers.size()];

        GameUtils.CLIENT_HANDELERS = clientHandlers;

        GameUtils.PROGRAM = GameUtils.SERVER;

        wc = new WorldContainer(GameUtils.VIEW_WIDTH, GameUtils.VIEW_HEIGHT);

    }


    public void start() {

        this.window = new Window(0.3f, "Server ingame");
        this.userInput = new UserInput(window, 1, 1);

        Font.loadFonts(FontType.BROADWAY);


        System.out.println("Server game initiated with clients: "+GameUtils.CLIENT_HANDELERS);

        //create entities
        GameUtils.assignComponentTypes(wc);

        GameUtils.assignSystems(wc, window, userInput);

        GameUtils.createMap(wc);

        AudioMaster.init();

        ArrayList<Integer> team1Chars = new ArrayList<>();
        ArrayList<Integer> team2Chars = new ArrayList<>();



        //TODO: Make generalized method of choosing characters with team mates. ServerCharacterSelection take teams into account
        int team1Character1 = charactersSelected.getCharacterIds().get(clientHandlers.get(0));
        int team2Character1 = charactersSelected.getCharacterIds().get(clientHandlers.get(1));

        team1Chars.add(team1Character1);
        team2Chars.add(team2Character1);

        CharacterUtils.createServerCharacters(wc, team1Chars, team2Chars);


        ServerClientHandler client1 = clientHandlers.get(0);
        ServerClientHandler client2 = clientHandlers.get(1);

        client1.sendInt(0); //team number
        client1.sendInt(team1Character1); //team 1 char
        client1.sendInt(team2Character1); //team 2 char

        client2.sendInt(1); //team number
        client2.sendInt(team1Character1); //team 1 char
        client2.sendInt(team2Character1); //team 2 char

        //print initial state


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

