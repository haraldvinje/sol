package game.client.clientStates;

import engine.PositionComp;
import engine.UserInput;
import engine.WorldContainer;
import engine.graphics.ColoredMeshComp;
import engine.graphics.ColoredMeshUtils;
import engine.graphics.ViewRenderComp;
import engine.graphics.text.Font;
import engine.graphics.text.FontType;
import engine.graphics.text.TextMesh;
import game.client.Client;
import engine.network.client.ClientState;
import engine.network.client.ClientStates;
import utils.maths.Vec4;

/**
 * Created by eirik on 26.07.2017.
 */
public class ClientIntroState extends ClientState {


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
        wc.addComponent(circ, new PositionComp(Client.CLIENT_WIDTH/2f, Client.CLIENT_HEIGHT/2f));
        wc.addComponent(circ, new ColoredMeshComp(ColoredMeshUtils.createCircleTwocolor(Client.CLIENT_HEIGHT*0.4f, 32)));

        int wt = wc.createEntity(); //welcome text
        wc.addComponent(wt, new PositionComp(100, 100));
        wc.addComponent(wt, new ViewRenderComp(new TextMesh("Welcome", Font.getFont(FontType.BROADWAY), 72, new Vec4(1, 1, 1, 1))));

        wt = wc.createEntity(); //connect text
        wc.addComponent(wt, new PositionComp(100, 200));
        wc.addComponent(wt, new ViewRenderComp(new TextMesh("Click to connect to server", Font.getFont(FontType.BROADWAY), 32, new Vec4(1, 1, 1, 0.7f))));
    }
}
