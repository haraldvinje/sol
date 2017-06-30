package engine.network.server;

import engine.PositionComp;
import engine.RotationComp;
import engine.Sys;
import engine.WorldContainer;
import engine.character.CharacterComp;
import engine.character.CharacterInputComp;
import engine.combat.abilities.AbilityComp;
import engine.combat.abilities.HitboxComp;
import engine.combat.abilities.ProjectileComp;
import engine.graphics.ColoredMeshComp;
import engine.graphics.ColoredMeshUtils;
import engine.network.CharacterInputData;
import engine.network.GameStateData;
import engine.network.NetworkUtils;

import java.util.*;

/**
 * Created by eirik on 21.06.2017.
 */
public class ServerNetworkSys implements Sys {

    private WorldContainer wc;

    private int frameNumber = 0; //Integer.MIN_VALUE;

    private List<ServerClientHandler> clientHandlers;



    public ServerNetworkSys(List<ServerClientHandler> clientHandlers) {

        this.clientHandlers = clientHandlers;

    }


    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.wc = wc;
    }

    @Override
    public void update() {

        updateCharactersByInput();

        updateClientsByGameState();

        frameNumber++;
        if (frameNumber == Integer.MAX_VALUE) throw new IllegalStateException("max frame number value reached. Make it wrap");
    }

    @Override
    public void terminate() {

    }



    private void updateCharactersByInput() {
        //update each character. WATCH THE ORDERING OF CHARACTERS CORRESPONDING TO CLIENTS
        //This should be done at the start of iteration.. if feels necessary...
        int i = 0;
        for (int entity : wc.getEntitiesWithComponentType(CharacterComp.class)) {
            if (clientHandlers.size() == 0 || (clientHandlers.size() == 1 && i==1) ) break; //<-----------------------------------------------UGLY

            //get input from each client, hoply corresponding to character ordering, and update the entity
            CharacterInputComp inpComp = (CharacterInputComp) wc.getComponent(entity, CharacterInputComp.class);
            CharacterInputData inData = clientHandlers.get(i).getInputData();

            if (inData != null) {
                //System.out.println("Got input data: "+inData);
                writeInDataToComp(inData, inpComp);
            }

            i++;
        }
    }

    private void updateClientsByGameState() {
        //get game state
        GameStateData stateData = retrieveGameState();

        //send game state
        sendGameState(stateData);
    }


    private GameStateData retrieveGameState() {
        GameStateData sd = new GameStateData();

        sd.setFrameNumber(frameNumber);

        Set<Integer> chars = wc.getEntitiesWithComponentType(CharacterComp.class);
        if (chars.size() != 2) throw new IllegalStateException("THere is not 2 characters on the field :(");

        int charNumb = 0;
        for (int c : chars) {
            PositionComp posComp = (PositionComp)wc.getComponent(c, PositionComp.class);
            RotationComp rotComp = (RotationComp)wc.getComponent(c, RotationComp.class);
            CharacterComp charComp = (CharacterComp)wc.getComponent(c, CharacterComp.class);
            AbilityComp abComp = (AbilityComp)wc.getComponent(c, AbilityComp.class);

            sd.setX(charNumb, posComp.getX());
            sd.setY(charNumb, posComp.getY());
            sd.setRotation(charNumb, rotComp.getAngle());

            //reset ability executed and terminated values
            sd.setAbilityExecuted(charNumb, -1); //default -1, no ability
            sd.setAbilityTerminated(charNumb, -1);

            //find newly executed abilities
            if (abComp.hasNewExecuting()) {
                sd.setAbilityExecuted(charNumb, abComp.popNewExecuting());
            }

            //find terminating projectiles. Only sends ONE for the moment, even though there might be more
            for (int entity : wc.getEntitiesWithComponentType(ProjectileComp.class)) {
                ProjectileComp projComp = (ProjectileComp)wc.getComponent(entity, ProjectileComp.class);
                HitboxComp hitbComp = (HitboxComp)wc.getComponent(entity, HitboxComp.class);

                //if the projectile dont belong to the current character, skip it
                if (hitbComp.getOwner() != c) continue;

                if (projComp.isShouldDeactivateFlag()) {
                    sd.setAbilityTerminated(charNumb, projComp.getAbilityId());
                    break; //as said before, it only finds ONE!
                }
            }

            charNumb++;
        }

        return sd;
    }


    public void sendGameState(GameStateData gameState) {
        ListIterator<ServerClientHandler> it = clientHandlers.listIterator();
        while (it.hasNext()) {
            ServerClientHandler handler = it.next();

            if (!handler.sendStateData(gameState) ) {
                //client has disconnected, remove it
                it.remove();
            }
        }
    }






    private void writeInDataToComp(CharacterInputData inData, CharacterInputComp inpComp) {
        inpComp.setMoveLeft( inData.isMoveLeft() );
        inpComp.setMoveRight( inData.isMoveRight() );
        inpComp.setMoveUp( inData.isMoveUp() );
        inpComp.setMoveDown( inData.isMoveDown() );

        inpComp.setAction1( inData.isAction1() );
        inpComp.setAction2( inData.isAction2() );
        inpComp.setAction3( inData.isAction3() );

        inpComp.setAimX( inData.getAimX() );
        inpComp.setAimY( inData.getAimY() );
    }


}
