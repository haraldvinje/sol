package engine.network.client;

import engine.*;
import engine.network.client.clientStates.*;
import engine.window.Window;

import java.io.IOException;
import java.net.Socket;
import java.util.EnumMap;

/**
 * Created by eirik on 21.06.2017.
 */
public class Client {

    public static final float WINDOW_WIDTH_SCALE = 0.7f, WINDOW_HEIGHT_SCALE = 0.7f;
    public static float WINDOW_WIDTH, WINDOW_HEIGHT;

    public static final float FRAME_INTERVAL = 1f/60f;

    private String hostname;
    private Socket socket = null;

    private Window window;
    private UserInput userInput;


    private ClientState currentState;
    private EnumMap<ClientStates, ClientState> states = new EnumMap<>(ClientStates.class);


    private long lastTime;
    private boolean running = true;



    public Client(String hostname) {
        this.hostname = hostname;
    }

    public void setSocket(Socket socket) {
        if (this.socket == null) {
            this.socket = socket;
        }
        else throw new IllegalStateException("Trying to set socket after it is already set");
    }

    public Socket getSocket() {
        return socket;
    }

    public void terminate() {
        running = false;
    }

    public void init() {
        window = new Window(WINDOW_WIDTH_SCALE, WINDOW_HEIGHT_SCALE, "SOL client");
        WINDOW_WIDTH = window.getWidth();
        WINDOW_HEIGHT = window.getHeight();

        userInput = new UserInput(window, window.getWidth(), window.getHeight());

        //assign states
        states.put(ClientStates.IDLE, new ClientIdleState());
        states.put(ClientStates.CONNECTING, new ClientConnectingState(hostname));
        states.put(ClientStates.WAITING_GAME, new ClientWaitingState());
        states.put(ClientStates.CHOOSING_CHARACTER, new ClientCharacterselectState());
        states.put(ClientStates.INGAME, new ClientIngameState() );

        gotoState(ClientStates.IDLE);
    }

    public void start() {

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

        //end current state
        currentState.onExit();

        close();
    }

    public void close() {
        try {
            if (socket != null) {
                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        Window.terminateGLFW();
    }


    public void update() {
        window.pollEvents();

        //update according to state
        currentState.onUpdate();

        //update current entities
        currentState.getWorldContainer().updateSystems();

        ClientStates nextState = currentState.popGotoState();
        if (nextState != null) {
            gotoState(nextState);
        }

    }

    private void gotoState(ClientStates newStateType) {
        if (currentState != null) {
            currentState.onExit();
        }

        ClientState newState = states.get(newStateType);
        if (!newState.isInitialized()) {
            newState.setClientFields(this, window, userInput);
            newState.init();
        }
        currentState = newState;

        //render new state
        currentState.getWorldContainer().updateSystems();

        currentState.onEnter();
    }

    public float getWindowWidth() {
        return window.getWidth();
    }
    public float getWindowHeight() {
        return window.getHeight();
    }

    private float timePassed() {
        long newTime = System.nanoTime();
        int deltaTime = (int)(newTime - lastTime);
        float deltaTimeF = (float) deltaTime;

        lastTime = newTime;

        return deltaTimeF/1000000000;
    }

}
