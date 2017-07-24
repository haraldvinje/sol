package game;

import engine.UserInput;
import engine.WorldContainer;
import engine.character.*;
import engine.combat.DamageResolutionSys;
import engine.combat.DamageableComp;
import engine.combat.DamagerComp;
import engine.graphics.*;
import engine.graphics.text.Font;
import engine.graphics.text.FontType;

import engine.character.CharacterComp;
import engine.network.client.ClientStateUtils;
import engine.network.server.ServerClientHandler;
import engine.physics.AffectedByHoleComp;
import engine.window.Window;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haraldvinje on 06-Jul-17.
 */
public class ServerGame implements Runnable {



    private ServerCharacterSelection characterSelection;
    private ServerInGame serverInGame;

    private List<ServerClientHandler> clients;



    private static final float FRAME_INTERVAL = 30.0f/60.0f;




    private boolean shouldTerminate = false;


    private UserInput userInput;


    private boolean running = true;

    private long lastTime;




    public void init( List<ServerClientHandler> clientHandlers) {

        clients = clientHandlers;

        characterSelection = new ServerCharacterSelection();

        GameUtils.CLIENT_HANDELERS = clientHandlers;

        GameUtils.PROGRAM = GameUtils.SERVER;

        wc = new WorldContainer(GameUtils.VIEW_WIDTH, GameUtils.VIEW_HEIGHT);


    }


    @Override
    public void run() {

        this.window = new Window(0.3f, "Server ingame");
        this.userInput = new UserInput(window, 1, 1);

        Font.loadFonts(FontType.BROADWAY);


        System.out.println("Server game initiated with clients: "+GameUtils.CLIENT_HANDELERS);

        //create entities
        GameUtils.assignComponentTypes(wc);

        GameUtils.assignSystems(wc, window, userInput);

        GameUtils.createMap(wc);

        ArrayList<Integer> team1Chars = new ArrayList<>();
        ArrayList<Integer> team2Chars = new ArrayList<>();
        team1Chars.add(0);
        team2Chars.add(1);
        CharacterUtils.createServerCharacters(wc, team1Chars, team2Chars);


        //print initial state
        System.out.println("Initial state:");
        System.out.println(wc.entitiesToString());

        lastTime = System.nanoTime();


        float timeSinceUpdate = 0;

        while (running) {
            timeSinceUpdate += timePassed();
            //System.out.println("Time since update: "+timeSinceUpdate);

            if (timeSinceUpdate >= FRAME_INTERVAL) {
                timeSinceUpdate -= FRAME_INTERVAL;
                update();
            }

        }


    }




    public void update() {

        for (ServerClientHandler client: clients){
            int characterSelectedId = client.getCharacterSelectedData();
            if (characterSelectedId!= -1){
                characterSelection.addCharacter(client, characterSelectedId);
            }
        }


        if (characterSelection.isReady()){
            serverInGame = new ServerInGame(characterSelection);
            serverInGame.init(clients);
            for (ServerClientHandler client: clients){
                client.sendClientStateId(ClientStateUtils.INGAME);
            }
            serverInGame.start(); //blocking until game ends
            characterSelection.getCharacterIds().clear();
        }




    }
    private void gameOver(int winner) {
        System.out.println("Player "+ winner + " won!");
        setShouldTerminate();

    }

    private void onTerminate() {
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
