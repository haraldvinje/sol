package engine.network.client;

import engine.*;
import engine.character.CharacterComp;
import engine.combat.abilities.AbilityComp;
import engine.combat.abilities.HitboxComp;
import engine.combat.abilities.ProjectileComp;
import engine.network.CharacterInputData;
import engine.network.CharacterStateData;
import engine.network.GameStateData;
import engine.network.NetworkUtils;
import utils.maths.M;

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

        NetworkUtils.characterInputToStream(id, outputStream); //protocol
    }

    private void applyGameState(GameStateData gameState) {
        //apply state

        System.out.println("Recieved frame: " + gameState.getFrameNumber());

        int entityNumb = 0;
        for (int entity : wc.getEntitiesWithComponentType(CharacterComp.class)) {
            if (entityNumb >= NetworkUtils.CHARACTER_NUMB) throw new IllegalStateException("applying game state to more characters than supported by network system");

            //PositionComp posComp = (PositionComp)wc.getComponent(entity, PositionComp.class);
            //RotationComp rotComp = (RotationComp)wc.getComponent(entity, RotationComp.class);
            CharacterComp charComp = (CharacterComp) wc.getComponent(entity, CharacterComp.class);

//            posComp.setX(gameState.getX(entityNumb));
//            posComp.setY(gameState.getY(entityNumb));
//            rotComp.setAngle(gameState.getRotation(entityNumb));

            //Give new character state to interpolation component
            InterpolationComp interpComp = (InterpolationComp)wc.getComponent(entity, InterpolationComp.class);
            interpComp.addFrame(gameState.getFrameNumber(), new CharacterStateData( gameState.getX(entityNumb), gameState.getY(entityNumb), gameState.getRotation(entityNumb) ));

            //execute abilities in abSystem according to input
            AbilityComp abComp = (AbilityComp) wc.getComponent(entity, AbilityComp.class);
            int newAbilityExecuting = gameState.getAbilityExecuted(entityNumb);
            if (newAbilityExecuting != -1){
                abComp.forceExecution(newAbilityExecuting);
                //System.out.println("Client detected shoot");
            }


            //deactivate projectile abilities. Only deactivates ONE!
            int projAbilityId = gameState.getAbilityTerminated(entityNumb);

            for (int projEntity : wc.getEntitiesWithComponentType(ProjectileComp.class)) {
                ProjectileComp projComp = (ProjectileComp)wc.getComponent(projEntity, ProjectileComp.class);
                HitboxComp hitbComp = (HitboxComp)wc.getComponent(projEntity, HitboxComp.class);

                //if projectile does not belong to current character, skip it
                if (hitbComp.getOwner() != entity) continue;

                if (projComp.getAbilityId() == projAbilityId) {

                    projComp.setShouldDeactivateFlag();
                    break; //as said before, it only finds ONE!
                }
            }

            entityNumb++;
        }

    }

    public GameStateData readGameState() {

        try {
            int messageBytes = GameStateData.BYTES;

            if (inputStream.available() >= messageBytes) {

                //remove delayed data
                while (inputStream.available() >= messageBytes*(2 + NetworkUtils.CLIENT_INPUT_BUFFERING) ) {
                    System.err.println("Skipping a game state to read newest state");
                    inputStream.skipBytes(messageBytes);
                }

                GameStateData gameState = NetworkUtils.streamToGameState(inputStream);

                return gameState;

            }
            else {
                //System.err.println("Not enough input for a gameState, numb of bytes ready: " + inputStream.available());
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("IO exception");
        }
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
