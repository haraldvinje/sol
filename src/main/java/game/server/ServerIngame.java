package game;

import engine.UserInput;
import engine.WorldContainer;
import engine.character.*;
import engine.graphics.text.Font;
import engine.graphics.text.FontType;
import engine.network.NetworkPregamePackets;
import engine.network.server.ServerClientHandler;
import engine.physics.*;
import engine.window.Window;

import java.util.*;


/**
 * Created by eirik on 13.06.2017.
 */
public class ServerInGame {


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



    public ServerInGame() {

    }


    public void init( ServerGame serverGame, ServerGameTeams teams) {
        this.serverGame = serverGame;
        this.teams = teams;


        //add an stockLoss entry for every client
        stockLossCount = new int[teams.getTotalClientCount()];

        GameUtils.CLIENT_HANDELERS = Arrays.asList( teams.getAllClients() );

        GameUtils.PROGRAM = GameUtils.SERVER;

        wc = new WorldContainer(GameUtils.VIEW_WIDTH, GameUtils.VIEW_HEIGHT);

    }


    public void start() {

        this.window = new Window(0.3f, "Server ingame");
        this.userInput = new UserInput(window, 1, 1);

        Font.loadFonts(FontType.BROADWAY);


        System.out.println("Server game initiated with clients: "+GameUtils.CLIENT_HANDELERS);


        GameUtils.assignComponentTypes(wc);

        GameUtils.assignSystems(wc, window, userInput);

        GameUtils.createMap(wc);

        CharacterUtils.createServerCharacters(wc, teams);


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


    private void createCharacters() {

//        //decode wich characters are on wich teams
//        List< List<Integer> > teamCharacters = new ArrayList<>();
//        teamCharacters.add(new ArrayList<>());
//        teamCharacters.add(new ArrayList<>());
//
//        for (int i = 0; i < teamClients.size(); i++) {
//            List<ServerClientHandler> team = teamClients.get(i);
//
//            for (int j = 0; j < team.size(); j++) {
//                ServerClientHandler client = team.get(j);
//
//                //find the character corresponding to the client and add to character list
//                int charId = clientCharacters.get(client);
//                teamCharacters.get(i).add(charId);
//            }
//        }



        //add characters to world container

    }

    private void sendInitialGameState(List< List<Integer> > teamCharacters) {

//        for (int i = 0; i < teamClients.size(); i++) {
//
//            List<ServerClientHandler> team = teamClients.get(i);
//
//            for (int j = 0; j < team.size(); j++) {
//                ServerClientHandler client = team.get(j);
//
//                //send state id of client
////                client.sendInt(NetworkPregamePackets.CHARSELECT_ID);
//
//                //send meassage id
//                client.sendInt(NetworkPregamePackets.CHARSELECT_SERVER_GOTO_GAME);
//
//                //send number of chars on team1
//                client.sendInt(teamCharacters.get(0).size());
//                //number of chars on team 2
//                client.sendInt(teamCharacters.get(1).size());
//
//                //clients team1 - team2
//                sendCharacterIds(client, teamCharacters);
//
//                //send wich character the client controls
//                //what team is the client on
//                client.sendInt(i);
//
//                //which index is he
//                client.sendInt(j);
//            }
//
//        }
    }

    private void sendCharacterIds(ServerClientHandler client, List< List<Integer>> teamCharacters) {
        for (int i = 0; i < teamCharacters.size(); i++) {
            List<Integer> team = teamCharacters.get(i);

            for (int j = 0; j < team.size(); j++) {

                client.sendInt( team.get(j) );
            }
        }
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

