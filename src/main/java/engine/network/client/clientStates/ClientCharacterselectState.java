package engine.network.client.clientStates;

import engine.PositionComp;
import engine.UserInput;
import engine.WorldContainer;
import engine.graphics.TexturedMeshComp;
import engine.graphics.TexturedMeshUtils;
import engine.network.client.Client;
import engine.network.client.ClientState;
import engine.network.client.ClientStates;

/**
 * Created by eirik on 04.07.2017.
 */
public class ClientCharacterselectState extends ClientState {


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
        if (userInput.isMousePressed(UserInput.MOUSE_BUTTON_1)) {

            setGotoState(ClientStates.INGAME);
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
