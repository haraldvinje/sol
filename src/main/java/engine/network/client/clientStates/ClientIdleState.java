package engine.network.client.clientStates;

import engine.PositionComp;
import engine.UserInput;
import engine.WorldContainer;
import engine.graphics.ColoredMeshComp;
import engine.graphics.ColoredMeshUtils;
import engine.network.client.Client;
import engine.network.client.ClientState;
import engine.network.client.ClientStates;

/**
 * Created by eirik on 04.07.2017.
 */
public class ClientIdleState extends ClientState {


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

            setGotoState(ClientStates.CONNECTING);
        }
    }

    @Override
    public void onExit() {

    }

    private void createInitialEntities(WorldContainer wc) {
        int circ = wc.createEntity();
        wc.addComponent(circ, new PositionComp(Client.WINDOW_WIDTH/2f, Client.WINDOW_HEIGHT/2f));
        wc.addComponent(circ, new ColoredMeshComp(ColoredMeshUtils.createCircleTwocolor(Client.WINDOW_HEIGHT*0.4f, 32)));
    }
}
