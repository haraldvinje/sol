package engine.network.client;

import engine.*;
import engine.character.CharacterComp;
import engine.network.CharacterInputData;
import engine.network.GameStateData;
import engine.network.NetworkUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by eirik on 21.06.2017.
 */
public class ClientNetworkSys implements Sys{


    private WorldContainer wc;
    private UserInput userInput;

    private Socket socket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;


    public ClientNetworkSys(String hostname, UserInput userInput) {

        try {
            System.out.println("Connecting to server");
            socket = new Socket(hostname, NetworkUtils.PORT_NUMBER);
            System.out.println("Connection established!");

            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream = new DataInputStream(socket.getInputStream());
        }
        catch (UnknownHostException e) {
            throw new IllegalArgumentException("Invalid hostname");
        }
        catch (IOException e) {
            throw new IllegalStateException("An io exception occured while setting up socket\n could not connect to specified host");
        }

        this.userInput = userInput;
    }

    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.wc = wc;
    }


    @Override
    public void update() {
        updateServerByInput();

        updateGameStateByServer();
    }

    @Override
    public void terminate() {

    }

    private void updateServerByInput() {

        CharacterInputData input = retrieveUserInput();
        sendInputData(input);
    }

    private void updateGameStateByServer() {

        GameStateData gameState = readGameState();
        if (gameState != null) {
            applyGameState(gameState);
        }
    }

    /**
     * retrieve input straight from user as of now
     * @return
     */
    public CharacterInputData retrieveUserInput() {
        CharacterInputData input = new CharacterInputData();

        input.setMoveLeft(userInput.isKeyboardPressed(UserInput.KEY_A));
        input.setMoveRight(userInput.isKeyboardPressed(UserInput.KEY_D));
        input.setMoveUp(userInput.isKeyboardPressed(UserInput.KEY_W));
        input.setMoveDown(userInput.isKeyboardPressed(UserInput.KEY_S));

        input.setAction1(userInput.isMousePressed(UserInput.MOUSE_BUTTON_1));
        input.setAction2(userInput.isMousePressed(UserInput.MOUSE_BUTTON_2));

        input.setAimX(userInput.getMouseX());
        input.setAimY(userInput.getMouseY());

        return input;
    }

    public void sendInputData(CharacterInputData id) {
        DataOutputStream ds = outputStream;

        try {
            ds.writeBoolean(id.isMoveLeft());
            ds.writeBoolean(id.isMoveRight());
            ds.writeBoolean(id.isMoveUp());
            ds.writeBoolean(id.isMoveDown());

            ds.writeBoolean(id.isAction1());
            ds.writeBoolean(id.isAction2());

            ds.writeFloat(id.getAimX());
            ds.writeFloat(id.getAimY());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void applyGameState(GameStateData gameState) {
        //apply state

        int entityNumb = 0;
        for (int entity : wc.getEntitiesWithComponentType(CharacterComp.class)) {
            PositionComp posComp = (PositionComp)wc.getComponent(entity, PositionComp.class);
            RotationComp rotComp = (RotationComp)wc.getComponent(entity, RotationComp.class);

            System.out.println("Updating game state by data: " + gameState);
            if (entityNumb == 0) {
                posComp.setX(gameState.getX1());
                posComp.setY(gameState.getY1());
                rotComp.setAngle(gameState.getRotation1());
            }
            else {
                posComp.setX(gameState.getX2());
                posComp.setY(gameState.getY2());
                rotComp.setAngle(gameState.getRotation2());
            }

            entityNumb++;
        }

    }

    public GameStateData readGameState() {

        try {
            int messageBytes = GameStateData.BYTES;

            if (inputStream.available() >= messageBytes) {

                //remove delayed data
                while (inputStream.available() >= messageBytes*2) {
                    inputStream.skipBytes(messageBytes);
                }

                return streamToGameState(inputStream);

            }
            else {
                System.err.println("Not enough input for a gameState, numb of bytes ready: " + inputStream.available());
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("IO exception");
        }
    }

    private GameStateData streamToGameState(DataInputStream in) {
        GameStateData state = new GameStateData();

        try {
            state.setX1(in.readFloat());
            state.setY1(in.readFloat());
            state.setRotation1(in.readFloat());

            state.setX2(in.readFloat());
            state.setY2(in.readFloat());
            state.setRotation2(in.readFloat());

            return state;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException();
    }



    //read character position, rotation,

    public void close() {
        try {
            outputStream.close();
            inputStream.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
