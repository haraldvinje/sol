package engine.network.client.clientStates;

import engine.PositionComp;
import engine.UserInput;
import engine.WorldContainer;
import engine.graphics.ColoredMeshComp;
import engine.graphics.ColoredMeshUtils;
import engine.graphics.MeshCenterComp;
import engine.network.client.Client;
import engine.network.client.ClientState;
import engine.network.client.ClientStates;

/**
 * Created by eirik on 04.07.2017.
 */
public class ClientWaitingState extends ClientState {

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

            setGotoState(ClientStates.CHOOSING_CHARACTER);
        }
    }

    @Override
    public void onExit() {

    }

    private void createInitialEntities(WorldContainer wc) {
        float width = Client.WINDOW_WIDTH/2;
        float height = Client.WINDOW_HEIGHT/6;

        int rect = wc.createEntity();
        wc.addComponent(rect, new PositionComp(Client.WINDOW_WIDTH/2f, Client.WINDOW_HEIGHT/2f));
        wc.addComponent(rect, new ColoredMeshComp(ColoredMeshUtils.createRectangle(width, height) ));
        wc.addComponent(rect, new MeshCenterComp(width/2f, height/2f));
    }
}