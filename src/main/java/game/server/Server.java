package engine.network.server;

import engine.PositionComp;
import engine.RotationComp;
import engine.UserInput;
import engine.WorldContainer;
import engine.graphics.*;
import engine.graphics.text.TextMeshComp;
import engine.graphics.view_.ViewControlComp;
import engine.network.NetworkPregamePackets;
import engine.network.NetworkUtils;
import engine.network.client.ClientStateUtils;
import engine.network.client.ClientStates;
import engine.window.Window;
import game.ServerGame;
import game.ServerGameTeams;
import game.ServerInGame;

import java.util.*;

/**
 * Created by eirik on 29.06.2017.
 */
public class Server {

    private static final float FRAME_INTERVAL = 1.0f/60.0f;


    private Window window;
    private UserInput userInput;

    private WorldContainer wc;

    private long lastTime;

    //clinet connection listener
    private ServerConnectionInput connectionInput;
    private Thread serverConnectionInputThread;

    //clients connected icons
    private LinkedList<Integer> allocatedClientIcons = new LinkedList<>();
    private Map<ServerClientHandler, Integer> activeClientIcons = new HashMap<>();

    //all connected clients
    private List<ServerClientHandler> connectedClients = new ArrayList<>();

    //idle clients
    private List<ServerClientHandler> idleClients = new ArrayList<>();

    //game queue
    private LinkedList<ServerClientHandler> gameQueue = new LinkedList<>();

    //games running
    private Map<ServerGame, Thread> gamesRunning = new HashMap<>();



    public void init() {
        window = new Window(0.3f, 0.3f, "D1n-only Server SII");
        userInput = new UserInput(window, 1, 1);

        wc = new WorldContainer();
        initWorldContainer();


        //create connection listener
        connectionInput = new ServerConnectionInput(NetworkUtils.PORT_NUMBER);
        serverConnectionInputThread = new Thread(connectionInput);


        //allocate client icons
        float iconStartX = 100, iconStartY = 100;
        float iconRadius = 64;
        for (int i = 0; i < NetworkUtils.CHARACTER_NUMB*3; i++) {
            allocatedClientIcons.add( allocateClientIcon(wc, iconStartX+iconRadius*2*i, iconStartY, iconRadius) );
        }
    }

    public void start() {

        serverConnectionInputThread.start();

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

        //poll window events
        window.pollEvents();


        //add pending connections to server state
        handleNewConnections();

        //handle clients that want to enter game queue
        handleIdleState();

        //handle game queue, and potentially start new games
        handleGameQueueState();


        //update systems to show server status
        wc.updateSystems();
    }

    private void handleNewConnections() {
        if (connectionInput.hasConnectedClients()) {

            System.out.println("New client connected");
            ServerClientHandler clientHandler = connectionInput.getConnectedClient();

            //put new client in list of clients and in the idle state
            connectedClients.add(clientHandler);
            idleClients.add(clientHandler);

//            activateClientIcon(clientHandler);
        }
    }

    private void handleIdleState() {
        idleClients.forEach(c -> c.getTcpPacketIn().pollPackets());


        //check if anyone wants to enter game queue
        Iterator<ServerClientHandler> it = idleClients.iterator();

        while(it.hasNext()) {
            ServerClientHandler client = it.next();

            if (client.getTcpPacketIn().removeIfHasPacket(NetworkPregamePackets.QUEUE_CLIENT_REQUEST_QUEUE)) {
                //remove from idle state and put into queue state
                it.remove();
                gameQueue.add(client);

                //let client know that it is put in queue
                client.getTcpPacketOut().sendEmpty(NetworkPregamePackets.QUEUE_SERVER_PUT_IN_QUEUE);

            }
        }
    }

    private void handleGameQueueState() {
        gameQueue.forEach(c -> c.getTcpPacketIn().pollPackets());


        //check if there are enough clients waiting to start a game
        if (gameQueue.size() >= 2) {

            System.out.println("At least two clients in game queue, starting game");

            //retrieve clients
            ServerClientHandler client1 = gameQueue.pop();
            ServerClientHandler client2 = gameQueue.pop();

            //let clients know that they are about to start game
            client1.getTcpPacketOut().sendEmpty(NetworkPregamePackets.QUEUE_SERVER_GOTO_CHARACTERSELECT);
            client2.getTcpPacketOut().sendEmpty(NetworkPregamePackets.QUEUE_SERVER_GOTO_CHARACTERSELECT);

            //datastructure to hold clients
            ServerGameTeams teams = new ServerGameTeams(client1, client2);

            createGame( teams );
        }


    }

    public void handleGamesRunning() {
        for (ServerGame game : gamesRunning.keySet()) {
            if (game.isShouldTerminate()) {
                terminateRunningGame(game);
            }
        }
    }

    public void terminate() {
        //terminate games running
        terminateAllRunningGames();


        wc.terminate();
        window.close();

        connectionInput.terminate();

        try {
            serverConnectionInputThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Window.terminateGLFW();
    }

    private void createGame(ServerGameTeams teams) {


        ServerGame game = new ServerGame();
        game.init(teams);

        Thread gameThread = new Thread(game);
        gamesRunning.put(game, gameThread);

        gameThread.start();
    }

    private void terminateAllRunningGames() {
        while (!gamesRunning.isEmpty()) {

            //retrieve a random element
            ServerGame game = gamesRunning.keySet().iterator().next();

            terminateRunningGame(game);
        }
    }

    private void terminateRunningGame(ServerGame game) {
        Thread gameThread = gamesRunning.remove(game); //retrieve thread and remove entry

        game.terminate();
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initWorldContainer() {
        wc.assignComponentType(PositionComp.class);
        wc.assignComponentType(RotationComp.class);
        wc.assignComponentType(ColoredMeshComp.class);
        wc.assignComponentType(TexturedMeshComp.class);
        wc.assignComponentType(MeshCenterComp.class);
        wc.assignComponentType(ViewControlComp.class);
        wc.assignComponentType(TextMeshComp.class);
        wc.assignComponentType(ViewRenderComp.class);


        wc.addSystem(new RenderSys(window));
    }




    private void activateClientIcon(ServerClientHandler clientHandeler) {
        if (allocatedClientIcons.isEmpty()) return;

        int icon = allocatedClientIcons.poll();
        wc.activateEntity(icon);
    }
    private void deactivateClientIcon(ServerClientHandler clientHandler) {
        if (! activeClientIcons.containsKey(clientHandler)) return;

        int icon = activeClientIcons.remove(clientHandler);
        allocatedClientIcons.add(icon);
        wc.deactivateEntity(icon);
    }

    private int allocateClientIcon(WorldContainer wc, float x, float y, float radius) {
        int e = wc.createEntity();
        wc.addInactiveComponent(e, new PositionComp(x, y));
        wc.addInactiveComponent(e, new ColoredMeshComp(ColoredMeshUtils.createCircleTwocolor(radius, 9)));

        return e;
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
