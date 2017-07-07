package engine.network.client.clientStates;

import engine.PositionComp;
import engine.UserInput;
import engine.WorldContainer;
import engine.graphics.ColoredMeshComp;
import engine.graphics.ColoredMeshUtils;
import engine.graphics.MeshCenterComp;
import engine.network.NetworkUtils;
import engine.network.client.Client;
import engine.network.client.ClientState;
import engine.network.client.ClientStates;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by eirik on 04.07.2017.
 */

public class ClientConnectingState extends ClientState {


    private String hostname;


    public ClientConnectingState(String hostname) {
        this.hostname = hostname;
    }

    @Override
    public void init() {
        super.init();

        createInitialEntities(wc);

    }

    @Override
    public void onEnter() {
        //do this on another thread.
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();

        }


        boolean connected = connectToServer(hostname);
        if (!connected) {
            System.err.println("Client could not connect to server");
            client.terminate();
        }

        setGotoState(ClientStates.WAITING_GAME);
    }

    @Override
    public void onUpdate() {



    }

    @Override
    public void onExit() {

    }

    private boolean connectToServer(String hostname) {
        try {
            System.out.println("Connecting to server");
            Socket socket = new Socket(hostname, NetworkUtils.PORT_NUMBER);
            System.out.println("Connection established!");

            client.setSocket(socket);
            return true;
        }
        catch (UnknownHostException e) {
            System.err.println("Invalid hostname");
        }
        catch (IOException e) {
            System.err.println("An io exception occured while setting up socket\n could not connect to specified host");
        }

        return false;
    }

    private void createInitialEntities(WorldContainer wc) {
        float width = Client.WINDOW_WIDTH/2, height = Client.WINDOW_HEIGHT/6;
        int rect = wc.createEntity();
        wc.addComponent(rect, new PositionComp(Client.WINDOW_WIDTH/2f, Client.WINDOW_HEIGHT/2f));
        wc.addComponent(rect, new ColoredMeshComp(ColoredMeshUtils.createRectangle(width, height) ));
        wc.addComponent(rect, new MeshCenterComp(width/2f, height/2f));
    }
}
