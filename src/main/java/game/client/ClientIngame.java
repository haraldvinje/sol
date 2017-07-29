package game.client;

import engine.UserInput;
import engine.WorldContainer;
import engine.audio.AudioMaster;
import engine.graphics.text.Font;
import engine.graphics.text.FontType;
import engine.graphics.view_.View;
import engine.network.TcpPacketInput;
import engine.network.TcpPacketOutput;
import engine.window.Window;
import game.CharacterUtils;
import game.ClientGameTeams;
import game.GameUtils;
import game.SysUtils;

import java.net.Socket;

/**
 * Created by eirik on 22.06.2017.
 */
public class ClientIngame implements Runnable{

    private static final float FRAME_INTERVAL = 1.0f/60.0f;

    public static final float WINDOW_WIDTH = 1600f, WINDOW_HEIGHT = 900f;

    private long lastTime;
    private boolean running = true;



    private TcpPacketInput tcpPacketIn;
    private TcpPacketOutput tcpPacketOut;

    private Window window;
    private UserInput userInput;

    private WorldContainer wc;


    private ClientGameTeams teams;



    public ClientIngame() {
    }

    public void init(TcpPacketInput tcpPacketIn, TcpPacketOutput tcpPacketOut, ClientGameTeams teams) {
        this.tcpPacketIn = tcpPacketIn;
        this.tcpPacketOut = tcpPacketOut;

        this.teams = teams;

        wc = new WorldContainer( new View(GameUtils.VIEW_WIDTH, GameUtils.VIEW_HEIGHT) );

        System.out.println("HEELLLLLOOOOO");
    }


    public void terminate() {
        running = false;
    }

    /**
     * blocking while the game runs
     */
    @Override
    public void run() {

        window = new Window(0.5f, 0.5f, "Client   SIIII");
        userInput = new UserInput(window, GameUtils.VIEW_WIDTH, GameUtils.VIEW_HEIGHT);


        //load stuff
        Font.loadFonts(FontType.BROADWAY);
        AudioMaster.init();



        GameUtils.assignComponentTypes(wc);

        GameUtils.createMap(wc);
        CharacterUtils.createClientCharacters(wc, teams);

        //do this afte rbecaus of onscreen sys wich creates entities..
        SysUtils.addClientSystems(wc, window, userInput, tcpPacketIn, tcpPacketOut);


        //print initial state
        System.out.println("Initial state:");
        System.out.println(wc.entitiesToString());


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
