package engine.network.client.clientStates;

import engine.PositionComp;
import engine.UserInput;
import engine.WorldContainer;
import engine.graphics.TexturedMeshComp;
import engine.graphics.TexturedMeshUtils;
import engine.network.client.Client;
import engine.network.client.ClientState;
import engine.network.client.ClientStateUtils;
import engine.network.client.ClientStates;

import java.io.IOException;

/**
 * Created by eirik on 04.07.2017.
 */
public class ClientCharacterselectState extends ClientState {


    //TODO: find out how to use characterId

    public static final int SHRANK_CHARACTER_ID = 0;
    public static final int SCHMATHIAS_CHARACTER_ID = 1;

    private boolean commited = false;

    private int characterSelected = 0;
    private boolean shrankSelected = false;

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


        if (!commited) {

            if (userInput.isMousePressed(UserInput.MOUSE_BUTTON_1)) {
                characterSelected = SHRANK_CHARACTER_ID;
                //send "Selected shrank" data to server
            } else if (userInput.isMousePressed(UserInput.MOUSE_BUTTON_2)) {
                characterSelected = SCHMATHIAS_CHARACTER_ID;

                //send "selected Schmathias" data to server socket
            }

            if (userInput.isKeyboardPressed(UserInput.KEY_ENTER)) {


                sendCharacterSelected(characterSelected);
                commited = true;
            }



        }

//        if (serverStartedGame()){
//            setGotoState(ClientStates.INGAME);
//        }



    }

    private boolean serverStartedGame() {
        try{
            if (client.getSocketInputStream().available()>=1){

               int goToIngame = client.getSocketInputStream().readInt();
               if (goToIngame!= ClientStateUtils.INGAME){
                    throw new IOException("Did not recieve ingame state from server. Wrong data recieved");
                }

                return true;
            }
        }
        catch (IOException e){
            System.out.println("Wrong bro");
        }

        return false;
    }

    private void sendCharacterSelected(int characterSelected) {
        try{
            client.getSocketOutputStream().writeInt(characterSelected);

        }
        catch (IOException e){
            System.out.println("Failed to write select shrank to server");
        }

    }



    @Override
    public void onExit() {

    }

    private void createInitialEntities(WorldContainer wc) {
        float characterSpace = 128;
        float iconCenterX = Client.WINDOW_WIDTH/2;
        float iconCenterY = Client.WINDOW_HEIGHT/2;

        int shrankIcon = wc.createEntity();
        wc.addComponent(shrankIcon, new PositionComp(iconCenterX - characterSpace/2, iconCenterY));
        wc.addComponent(shrankIcon, new TexturedMeshComp(TexturedMeshUtils.createRectangle("sol_frank.png", 100, 100)));

        int schmathiasIcon = wc.createEntity();
        wc.addComponent(schmathiasIcon, new PositionComp(iconCenterX + characterSpace/2, iconCenterY));
        wc.addComponent(schmathiasIcon, new TexturedMeshComp(TexturedMeshUtils.createRectangle("Schmathias.png", 100, 100)));
    }
}
