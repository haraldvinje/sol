package game;

import engine.UserInput;
import engine.network.NetworkDataInput;
import engine.network.NetworkDataOutput;
import engine.network.NetworkPregamePackets;
import engine.network.NetworkPregameUtils;
import engine.network.server.ServerClientHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by haraldvinje on 06-Jul-17.
 */
public class ServerGame implements Runnable {



    private ServerCharacterSelection characterSelection;
    private ServerInGame serverInGame;

//    private List< List<ServerClientHandler> > teamClients;
//
//    private HashMap<ServerClientHandler, Integer> clientsCharacter;

    private ServerGameTeams teams;


    private static final float FRAME_INTERVAL = 1.0f/60.0f;


    private boolean running = true;
    private boolean shouldTerminate = false;

    private UserInput userInput;

    private long lastTime;


    private boolean selectingCharacter = true;
    private int waitingClientsIngame = -1;


    public void init( ServerGameTeams teams ) {

        this.teams = teams;

//        //create a character selection handle state
//        characterSelection = new ServerCharacterSelection();

        //eehhh
//        List<ServerClientHandler> clients = new ArrayList<>();
//        clients.addAll(teamClients.get(0));
//        clients.addAll(teamClients.get(1));
//        GameUtils.CLIENT_HANDELERS = clients;
//
//        //eehhh
//        GameUtils.PROGRAM = GameUtils.SERVER;


    }


    @Override
    public void run() {


        float timeSinceUpdate = 0;

        while (running) {
            timeSinceUpdate += timePassed();
            //System.out.println("Time since update: "+timeSinceUpdate);

            if (timeSinceUpdate >= FRAME_INTERVAL) {
                timeSinceUpdate -= FRAME_INTERVAL;
                update();
            }

        }

        onTerminate();

    }




    public void update() {

        //poll packets for all clients
        teams.forEachClient(c -> c.getTcpPacketIn().pollPackets());

        handleCharacterSelect();

        handleWaitForClientsIngame();
    }

    private void handleCharacterSelect() {
        if (!selectingCharacter) return;

        //retrieve selections
        teams.forEachClient(client -> {
            NetworkDataInput data = client.getTcpPacketIn().pollPacket(NetworkPregamePackets.CHARSELECT_CLIENT_CHOSE_CHARACTER);

            if (data != null) {
                int characterId = data.readInt();

                teams.setClientCharacterId(client, characterId);
            }
        });

        if (!teams.hasCharacterSelectionRemaining()) {
            //done selecting character, initialize game
            selectingCharacter = false;
            System.out.println("Done selecting character");

            initGame();
        }
    }

    private void handleWaitForClientsIngame() {
        if (selectingCharacter) return;
        if (waitingClientsIngame <= 0) return;

        //check if clients are ingame. If so, send init client team data
        teams.forEachClient(client -> {
            if ( client.getTcpPacketIn().removeIfHasPacket(NetworkPregamePackets.INGAME_CLIENT_READY) ) {

                //ingame received, send client teams to client
                ClientGameTeams clientTeams = teams.getClientGameTeams(client);
                NetworkDataOutput dataOut = NetworkPregameUtils.clientGameTeamsToPacket(clientTeams);
                client.getTcpPacketOut().send(NetworkPregamePackets.INGAME_SERVER_CLIENT_GAME_TEAMS, dataOut);

                //update how many clients we are waiting for
                --waitingClientsIngame;
            }
        });

        //check if all clients are ingame
        if (waitingClientsIngame == 0) {

            //blocking until game ends
            serverInGame.start();
            characterSelection.getCharacterIds().clear();
        }

    }

    private void initGame() {
        //init game
        System.out.println("Initializing inGame, waiting for clients");
        serverInGame = new ServerInGame();
        serverInGame.init(this, teams);

        //tell clients to go to ingame
        teams.forEachClient(client -> {

            client.getTcpPacketOut().sendEmpty(NetworkPregamePackets.CHARSELECT_SERVER_GOTO_GAME);

        });

        //start waiting for clients to report ingame
        waitingClientsIngame = teams.getTotalClientCount();
    }

    private void gameOver(int winner) {
        System.out.println("Player "+ winner + " won!");
        setShouldTerminate();

    }

    private void onTerminate() {
    }

    void setShouldTerminate() {
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
        if (serverInGame != null)
            serverInGame.terminate();

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