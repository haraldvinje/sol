package game.client.clientStates;

import engine.PositionComp;
import engine.UserInput;
import engine.WorldContainer;
import engine.graphics.*;
import engine.network.NetworkDataOutput;
import engine.network.NetworkPregamePackets;
import game.client.Client;
import engine.network.client.ClientState;
import engine.network.client.ClientStates;
import game.CharacterUtils;
import utils.maths.Vec2;

/**
 * Created by eirik on 04.07.2017.
 */
public class ClientCharacterselectState extends ClientState {


    private int[] characterIconEntities;

    private int cursorEntity;
    private int commitEntity;

    private boolean characterCommited;
    private int characterSelected;


    private float characterSpace = 200;
    private float iconCenterX = Client.CLIENT_WIDTH/2;
    private float iconCenterY = Client.CLIENT_HEIGHT/2;

    @Override
    public void init() {
        super.init();

        characterIconEntities = new int[CharacterUtils.CHARACTER_COUNT];
        createInitialEntities(wc);
    }

    @Override
    public void onEnter() {

        characterCommited = false;
        characterSelected = CharacterUtils.SHRANK;

        wc.deactivateEntity(commitEntity);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onExit() {

    }


    @Override
    public void onUpdate() {

        handleCharacterselect();
        handleCursorPosition();

        //if input obtained, goto game
        handleServerInput();

    }


    private void handleCharacterselect() {
        if (characterCommited) return;

        //decrease character selected id
        if (userInput.isKeyboardPressed(UserInput.KEY_LEFT)) {
            if (characterSelected > 0) {
                --characterSelected;
            }
        }
        //increase character selected id
        else if (userInput.isKeyboardPressed(UserInput.KEY_RIGHT)) {
            if (characterSelected < CharacterUtils.CHARACTER_COUNT - 1) {
                ++characterSelected;
            }
        }
        else if (userInput.isKeyboardPressed(UserInput.KEY_ENTER)) {

            characterCommited = true;

            //send data to server that character is selected
            sendCharacterSelected(characterSelected);

            //change cursor
            commitCursor();
        }

    }

    private void handleCursorPosition() {
        //set cursor position according to character

        //if we have commited dont need to do this
        if (characterCommited) {
            return;
        }

        //get selected character position
        PositionComp charPosComp = (PositionComp) wc.getComponent(characterSelected, PositionComp.class);

        //get cursor position
        PositionComp cursPosComp = (PositionComp) wc.getComponent(cursorEntity, PositionComp.class);

        //set cursor position
        float offsetX = 64, offsetY = 64;
        cursPosComp.setPos(charPosComp.getPos().add(new Vec2(offsetX, offsetY)));
    }

    private boolean handleServerInput() {

        if (client.getTcpPacketIn().removeIfHasPacket(NetworkPregamePackets.CHARSELECT_SERVER_GOTO_GAME)) {
            setGotoState(ClientStates.INGAME);
        }

        return true;
    }


    private void commitCursor() {
        PositionComp cursorPosComp = (PositionComp) wc.getComponent(cursorEntity, PositionComp.class);

        wc.activateEntity(commitEntity);
        PositionComp commitPosComp = (PositionComp) wc.getComponent(commitEntity, PositionComp.class);
        commitPosComp.setPos(cursorPosComp.getPos());
    }


    private void sendCharacterSelected(int characterSelected) {
        NetworkDataOutput data = new NetworkDataOutput();
        data.writeInt(characterSelected);

        client.getTcpPacketOut().send(NetworkPregamePackets.CHARSELECT_CLIENT_CHOSE_CHARACTER, data);
    }



    private void createInitialEntities(WorldContainer wc) {
//        this.characterSpace = 128;
//        this.iconCenterX = Client.CLIENT_WIDTH/2;
//        this.iconCenterY = Client.CLIENT_HEIGHT/2;

        //create character icons
        int shrankIcon = wc.createEntity();
        wc.addComponent(shrankIcon, new PositionComp(iconCenterX - characterSpace/2, iconCenterY));
        wc.addComponent(shrankIcon, new TexturedMeshComp(TexturedMeshUtils.createRectangle("sol_frank.png", 100, 100)));

        int schmathiasIcon = wc.createEntity();
        wc.addComponent(schmathiasIcon, new PositionComp(iconCenterX + characterSpace/2, iconCenterY));
        wc.addComponent(schmathiasIcon, new TexturedMeshComp(TexturedMeshUtils.createRectangle("Schmathias.png", 100, 100)));

        //store character icons in list
        characterIconEntities[CharacterUtils.SHRANK] = shrankIcon;
        characterIconEntities[CharacterUtils.SCHMATHIS] = schmathiasIcon;


        this.cursorEntity  = wc.createEntity();
        wc.addComponent(cursorEntity, new PositionComp(iconCenterX - characterSpace/2 + 30, iconCenterY + 150));
        float[] red = {1f,0f,0f};
        wc.addComponent(cursorEntity, new ColoredMeshComp(ColoredMeshUtils.createCircleSinglecolor(20, 16, red)));

        commitEntity = wc.createEntity();
        float[]green = {0f, 1f, 0f};
        wc.addComponent(commitEntity, new PositionComp(0, 0));
        wc.addComponent(commitEntity, new ColoredMeshComp(ColoredMeshUtils.createCircleSinglecolor(20, 16, green)));


    }
}
