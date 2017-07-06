package engine.network.client;

import engine.Sys;
import engine.WorldContainer;
import engine.character.CharacterComp;
import engine.combat.abilities.AbilityComp;
import engine.combat.abilities.HitboxComp;
import engine.combat.abilities.ProjectileComp;
import engine.network.CharacterStateData;
import engine.network.GameStateData;
import engine.network.NetworkUtils;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by eirik on 04.07.2017.
 */
public class ClientNetworkInSys implements Sys{

    private WorldContainer wc;

    private DataInputStream inputStream;


    public ClientNetworkInSys(Socket socket) {

        try {
            inputStream = new DataInputStream(socket.getInputStream());
        }
        catch (IOException e) {
            throw new IllegalStateException("An io exception occured while setting up socket\n could not connect to specified host");
        }
    }

    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.wc = wc;
    }

    @Override
    public void update() {
        updateGameStateByServer();
    }

    @Override
    public void terminate() {
//        try {
//            inputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    private void updateGameStateByServer() {

        GameStateData gameState = readGameState();
        if (gameState != null) {
            applyGameState(gameState);
        }
    }



    private void applyGameState(GameStateData gameState) {
        //apply state

        //System.out.println("Recieved frame: " + gameState.getFrameNumber());

        //update each character according to state received
        int entityNumb = 0;
        for (int entity : wc.getEntitiesWithComponentType(CharacterComp.class)) {
            if (entityNumb >= NetworkUtils.CHARACTER_NUMB) throw new IllegalStateException("applying game state to more characters than supported by network system");

            CharacterComp charComp = (CharacterComp) wc.getComponent(entity, CharacterComp.class);

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
                    //System.err.println("Skipping a game state to read newest state");
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
}
