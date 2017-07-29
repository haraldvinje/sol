package game.client.clientStates;

import engine.PositionComp;
import engine.graphics.ViewRenderComp;
import engine.graphics.text.Font;
import engine.graphics.text.FontType;
import engine.graphics.text.TextMesh;
import engine.network.client.*;
import utils.maths.Vec4;

/**
 * Created by eirik on 04.07.2017.
 */
public class ClientIdleState extends ClientState {


    private int welcomeTextEntity;
    private int connectedTextEntity;

    private String[] buttonTexts = {"Play", "About the game"};
    private int[] buttons = new int[buttonTexts.length];
    private OnButtonAction[] buttonReleaseActions;

    private float buttonsLeft = 400, buttonsTop = 450;
    private float buttonWidth = 500, buttonHeight = 60, buttonVertSpace = 10;

    private String welcomeString = "You are connected!";
    private String connectedString = "WELCOME";


    private final int welcomeFadeTime = 60;
    private int welcomeFadeTimer;


    @Override
    public void init() {
        super.init();

        OnButtonAction[] ba = {
                (action, entity) -> setGotoState(ClientStates.WAITING_GAME),
                (action, entity) -> System.out.println("Should now goto character state")
        };
        buttonReleaseActions = ba;

        createInitialEntities();
    }

    @Override
    public void onEnter() {
        //print welcome string on screen, and start fade timer
        ClientUtils.setEntityString(wc, welcomeTextEntity, welcomeString);
        welcomeFadeTimer = welcomeFadeTime;

        ClientUtils.setEntityString(wc, connectedTextEntity, connectedString);

    }

    @Override
    public void onUpdate() {
        if (welcomeFadeTimer >= 0) {
            float colorAlpha = (float)welcomeFadeTimer / (float)welcomeFadeTime;
            ClientUtils.getEntityTextMesh(wc, welcomeTextEntity).getColor().w = colorAlpha;

            //if welcome text is invisible, remove it
            if (welcomeFadeTimer == 0) {
                ClientUtils.setEntityString(wc, welcomeTextEntity, "");
            }

            --welcomeFadeTimer;
        }
    }

    @Override
    public void onExit() {


    }

    private void createInitialEntities() {
        welcomeTextEntity = wc.createEntity();
        wc.addComponent(welcomeTextEntity, new PositionComp(400, 100));
        wc.addComponent(welcomeTextEntity, new ViewRenderComp(new TextMesh("", Font.getFont(FontType.BROADWAY), 64, new Vec4(0.8f, 1f, 0.8f, 1f))));

        connectedTextEntity = wc.createEntity();
        wc.addComponent(connectedTextEntity, new PositionComp(400, 300));
        wc.addComponent(connectedTextEntity, new ViewRenderComp(new TextMesh("", Font.getFont(FontType.BROADWAY), 72, new Vec4(0.8f, 1f, 0.8f, 1f))));

        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = ClientUtils.createButton(wc,
                    buttonsLeft, buttonsTop+ i*(buttonHeight+buttonVertSpace), buttonWidth, buttonHeight,
                    new TextMesh(buttonTexts[i], Font.getDefaultFont(), 54, new Vec4(0.9f, 0.9f, 0.9f, 1f)),
                    null, buttonReleaseActions[i], null, null
            );
        }
    }

}
