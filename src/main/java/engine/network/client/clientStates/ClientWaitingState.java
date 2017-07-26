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
import engine.network.client.ClientStateUtils;
import engine.network.client.ClientStates;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by eirik on 04.07.2017.
 */
public class ClientWaitingState extends ClientState {

    private boolean ready = false;

    @Override
    public void init() {
        super.init();

        createInitialEntities(wc);
    }

    @Override
    public void onEnter() {

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdate() {

        //TODO: Go to choosing character when two clients connected to same server
        try {
            if (client.getSocketInputStream().available()!=0){
                int characterSelectId = client.getSocketInputStream().readInt();
                if (characterSelectId==ClientStateUtils.CHOOSING_CHARACTER){
                    setGotoState(ClientStates.CHOOSING_CHARACTER);
                }
                else{
                    throw new IOException("Unexpected data received from server.");
                }
                //Server has two clients, and the client can now go to CharacterSelection

            }

        }
        catch (IOException e){
            e.printStackTrace();
            System.err.println("Faiiil");
        }

    }



    @Override
    public void onExit() {

    }





    private void createInitialEntities(WorldContainer wc) {
        float width = Client.CLIENT_WIDTH/2;
        float height = Client.CLIENT_HEIGHT/6;

        int rect = wc.createEntity();
        wc.addComponent(rect, new PositionComp(Client.CLIENT_WIDTH/2f, Client.CLIENT_HEIGHT/2f));
        wc.addComponent(rect, new ColoredMeshComp(ColoredMeshUtils.createRectangle(width, height) ));
        wc.addComponent(rect, new MeshCenterComp(width/2f, height/2f));
    }
}
