package engine.network.client.clientStates;

import engine.PositionComp;
import engine.UserInput;
import engine.WorldContainer;
import engine.graphics.*;
import engine.network.client.Client;
import engine.network.client.ClientState;
import engine.network.client.ClientStateUtils;
import engine.network.client.ClientStates;
import utils.maths.Vec2;

import javax.annotation.PostConstruct;
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
    private int cursor;
    private float characterSpace;
    private float iconCenterX;
    private float iconCenterY;

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
                setCursorOnShrank();
                //send "Selected shrank" data to server
            } else if (userInput.isMousePressed(UserInput.MOUSE_BUTTON_2)) {
                characterSelected = SCHMATHIAS_CHARACTER_ID;
                setCursorOnSchmathias();
                //send "selected Schmathias" data to server socket
            }

            if (userInput.isKeyboardPressed(UserInput.KEY_ENTER)) {


                sendCharacterSelected(characterSelected);
                commited = true;
                commitCursor();
            }



        }

        if (serverStartedGame()){
            setGotoState(ClientStates.INGAME);
        }



    }

    private void commitCursor() {
        ColoredMeshComp colMeshComp = (ColoredMeshComp)wc.getComponent(cursor, ColoredMeshComp.class);
        float[]green = {0f, 1f, 0f};
        colMeshComp.setMesh(ColoredMeshUtils.createCircleSinglecolor(20, 16, green));
    }

    private void setCursorOnShrank() {
        PositionComp posComp = (PositionComp)wc.getComponent(cursor, PositionComp.class);
        posComp.setPos(new Vec2(iconCenterX - characterSpace/2 + 30, iconCenterY + 150));
    }

    private void setCursorOnSchmathias(){
        PositionComp posComp = (PositionComp)wc.getComponent(cursor, PositionComp.class);
        posComp.setPos(new Vec2(iconCenterX + characterSpace/2 + 30, iconCenterY + 150));
    }

    private boolean serverStartedGame() {
        try{
            if (client.getSocketInputStream().available()>=1){
               int goToIngame = client.getSocketInputStream().readInt();
               if (goToIngame == ClientStateUtils.INGAME){
                   return true;
                }
                else{
                   throw new IOException("Did not recieve ingame state from server. Wrong data recieved");

               }
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
            System.out.println("Failed to send characterSelectedData to server");
        }

    }



    @Override
    public void onExit() {

    }

    private void createInitialEntities(WorldContainer wc) {
        this.characterSpace = 128;
        this.iconCenterX = Client.WINDOW_WIDTH/2;
        this.iconCenterY = Client.WINDOW_HEIGHT/2;

        int shrankIcon = wc.createEntity();
        wc.addComponent(shrankIcon, new PositionComp(iconCenterX - characterSpace/2, iconCenterY));
        wc.addComponent(shrankIcon, new TexturedMeshComp(TexturedMeshUtils.createRectangle("sol_frank.png", 100, 100)));

        int schmathiasIcon = wc.createEntity();
        wc.addComponent(schmathiasIcon, new PositionComp(iconCenterX + characterSpace/2, iconCenterY));
        wc.addComponent(schmathiasIcon, new TexturedMeshComp(TexturedMeshUtils.createRectangle("Schmathias.png", 100, 100)));

        this.cursor  = wc.createEntity();
        wc.addComponent(cursor, new PositionComp(iconCenterX - characterSpace/2 + 30, iconCenterY + 150));
        float[] red = {1f,0f,0f};
        wc.addComponent(cursor, new ColoredMeshComp(ColoredMeshUtils.createCircleSinglecolor(20, 16, red)));
    }
}
