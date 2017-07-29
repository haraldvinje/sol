package game.client;

import engine.UserInput;
import engine.WorldContainer;
import engine.graphics.text.Font;
import engine.graphics.text.FontType;
import engine.network.TcpPacketInput;
import engine.network.TcpPacketOutput;
import engine.window.Window;
import game.CharacterUtils;
import game.ClientGameTeams;
import game.GameUtils;

import java.net.Socket;

/**
 * Created by eirik on 22.06.2017.
 */
public class ClientGame implements Runnable{

    private static final float FRAME_INTERVAL = 1.0f/60.0f;

    public static final float WINDOW_WIDTH = 1600f, WINDOW_HEIGHT = 900f;



    private Window window;
    private UserInput userInput;

    private Socket socket;


    private long lastTime;

    private WorldContainer wc;

    private boolean running = true;



    private ClientGameTeams teams;
//    private List<Integer> friendlyCharacters, enemyCharacters;
//    private int clientCharacterId;
//    private int team;



    public ClientGame() {
    }
    public ClientGame(Socket socket) {
        this.socket = socket;
    }


    public void init(TcpPacketInput tcpPacketIn, TcpPacketOutput tcpPacketOut, ClientGameTeams teams) {

        this.teams = teams;

        wc = new WorldContainer(GameUtils.VIEW_WIDTH, GameUtils.VIEW_HEIGHT);

        System.out.println("HEELLLLLOOOOO");

        //set program state
        GameUtils.PROGRAM = GameUtils.CLIENT;
        GameUtils.socket = socket;

    }

    public void terminate() {
        running = false;
    }

    /**
     * blocking while the game runs
     */
    @Override
    public void run() {

        System.out.println("Running client");
        window = new Window(0.5f, 0.5f, "Client   SIIII");
        userInput = new UserInput(window, GameUtils.VIEW_WIDTH, GameUtils.VIEW_HEIGHT);

        GameUtils.assignComponentTypes(wc);

        GameUtils.createMap(wc);

        CharacterUtils.createClientCharacters(wc, teams);



        Font.loadFonts(FontType.BROADWAY);

        GameUtils.assignSystems(wc, window, userInput);


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


            if (window.shouldClosed() || userInput.isKeyboardPressed(UserInput.KEY_ESCAPE))
                break;
        }

        close();

    }


    public void update() {

        window.pollEvents();

        wc.updateSystems();
    }

    private void close() {

        window.close();
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
