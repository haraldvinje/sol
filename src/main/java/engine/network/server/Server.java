package engine.network.server;

import engine.PositionComp;
import engine.RotationComp;
import engine.UserInput;
import engine.WorldContainer;
import engine.graphics.*;
import engine.graphics.text.TextMeshComp;
import engine.graphics.view_.ViewControlComp;
import engine.network.NetworkUtils;
import engine.network.client.ClientStateUtils;
import engine.network.client.ClientStates;
import engine.window.Window;
import game.ServerGame;
import game.ServerInGame;

import java.util.*;

/**
 * Created by eirik on 29.06.2017.
 */
public class Server {

    private static final float FRAME_INTERVAL = 1.0f/60.0f;



    private ServerConnectionInput connectionInput;
    private Thread serverConnectionInputThread;

    private LinkedList<Integer> allocatedClientIcons = new LinkedList<>();
    private Map<ServerClientHandler, Integer> activeClientIcons = new HashMap<>();


    private Window window;
    private UserInput userInput;

    private WorldContainer wc;

    private long lastTime;


    private LinkedList<ServerClientHandler> clientsWaiting = new LinkedList<>();

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

        checkNewConnections(); //adds panding connections

        if (clientsWaiting.size() >= 2) {

            System.out.println("Two Clients Waiting");
            createGame(clientsWaiting.pop(), clientsWaiting.pop());
        }

        for (ServerGame game : gamesRunning.keySet()) {
            if (game.isShouldTerminate()) {
                terminateRunningGame(game);
            }
        }

        window.pollEvents();

        wc.updateSystems();
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

    private void createGame(ServerClientHandler client1, ServerClientHandler client2) {

        ServerGame game = new ServerGame();


        ServerClientHandler[] clients = {client1, client2};
        ArrayList<ServerClientHandler> clientList = new ArrayList<>( Arrays.asList(clients) );


        client1.sendClientStateId(ClientStateUtils.CHOOSING_CHARACTER);
        client2.sendClientStateId(ClientStateUtils.CHOOSING_CHARACTER);

        game.init(clientList);



        Thread gameThread = new Thread(game);

        gamesRunning.put(game, gameThread);

        gameThread.start();

        client1.sendInt(0); //team number
        client1.sendInt(0); //team 1 char
        client1.sendInt(1); //team 2 char

        client2.sendInt(1); //team number
        client2.sendInt(0); //team 1 char
        client2.sendInt(1); //team 2 char

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


    private void checkNewConnections() {
        if (connectionInput.hasConnectedClients()) {
            System.out.println("Retrieving new connection. clientWaitng="+clientsWaiting.size());
            ServerClientHandler clientHandler = connectionInput.getConnectedClient();
            clientsWaiting.add(clientHandler);

            activateClientIcon(clientHandler);
        }
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
