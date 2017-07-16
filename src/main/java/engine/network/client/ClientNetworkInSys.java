package engine.network.client;

import engine.PositionComp;
import engine.Sys;
import engine.WorldContainer;
import engine.character.CharacterComp;
import engine.combat.abilities.AbilityComp;
import engine.combat.abilities.HitboxComp;
import engine.combat.abilities.ProjectileComp;
import engine.graphics.RenderSys;
import engine.graphics.text.Font;
import engine.graphics.text.FontType;
import engine.graphics.text.TextMesh;
import engine.graphics.text.TextMeshComp;
import engine.network.*;
import engine.visualEffect.VisualEffectComp;
import game.GameUtils;
import utils.maths.Vec4;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by eirik on 04.07.2017.
 */
public class ClientNetworkInSys implements Sys{



    private WorldContainer wc;

    private DataInputStream inputStream;

//    private List<Integer> inPacketSizes;

    private int nextPacketType = -1;



    public ClientNetworkInSys(Socket socket) {
        //init packet sizes
//        Integer[] inPacketSizes = {AllCharacterStateData.BYTES, AbilityStartedData.BYTES, HitDetectedData.BYTES, ProjectileDeadData.BYTES, EntityDeadData.BYTES};
//        this.inPacketSizes = Arrays.asList(inPacketSizes);

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

        List<AllCharacterStateData> characterStates = new ArrayList<>();

        while (true) {
            if (inBytesAvailable() < Integer.BYTES) {
                break;
            }

            if (nextPacketType == -1) {
                nextPacketType = readNextPacketId();

//                System.out.println("Read new packet id = " + nextPacketType);
            }

            boolean notEnoughData = false;

            switch (nextPacketType) {
                case NetworkUtils.SERVER_CHARACTER_STATE_ID:

                    AllCharacterStateData data = readCharacterData();
                    if (data != null) {
                        nextPacketType = -1; //read new packet type
                        characterStates.add(data);
                    }
                    else {
                        notEnoughData = true;
                    }

                    break;

                case NetworkUtils.SERVER_ABILITY_STARTED_ID:

                    AbilityStartedData abData = readAbilityStarted();
                    if (abData != null) {
                        nextPacketType = -1;
                        applyAbilityStarted(abData);
                    }
                    else {
                        notEnoughData = true;
                    }

                    break;

                case NetworkUtils.SERVER_HIT_DETECTED_ID:

                    HitDetectedData hitData = readHitDetected();
                    if (hitData != null) {
                        nextPacketType = -1;
                        applyHitDetected(hitData);
                    }
                    else {
                        notEnoughData = true;
                    }
                    break;

                case NetworkUtils.SERVER_PROJECTILE_DEAD_ID:

                    ProjectileDeadData projDeadData = readProjectileDead();
                    if (projDeadData != null) {
                        nextPacketType = -1;
                        applyProjectileDead(projDeadData);
                    }
                    else {
                        notEnoughData = true;
                    }
                    break;

                default:
                    throw new IllegalStateException("Got a game packet with unknown identity");
            }

            if (notEnoughData) break;

            if (characterStates.size() >= 1) {
                applyCharacterStates(characterStates.get(characterStates.size() - 1));
            }
        }

    }



    private void applyCharacterStates(AllCharacterStateData gameState) {
        //apply state

        //System.out.println("Recieved frame: " + gameState.getFrameNumber());

        //update each character according to state received
        int entityNumb = 0;
        for (int entity : wc.getEntitiesWithComponentType(CharacterComp.class)) {
            if (entityNumb >= NetworkUtils.CHARACTER_NUMB) throw new IllegalStateException("applying game state to more characters than supported by network system");

            //Give new character state to interpolation component
            InterpolationComp interpComp = (InterpolationComp)wc.getComponent(entity, InterpolationComp.class);

            interpComp.addFrame(gameState.getFrameNumber(), new CharacterStateData( gameState.getX(entityNumb), gameState.getY(entityNumb), gameState.getRotation(entityNumb) ));

            entityNumb++;
        }

    }

    private void applyAbilityStarted(AbilityStartedData data) {
        int abilityEntity = data.getEntityId();
        int abilityId = data.getAbilityId();

        System.out.println("apllying ability started; "+data);

        AbilityComp abComp = (AbilityComp) wc.getComponent(abilityEntity, AbilityComp.class);
        abComp.forceExecution(abilityId);

    }
    private void applyHitDetected(HitDetectedData data) {

        System.out.println("Hit detected: "+data);

        int entityDamager = data.getEntityDamager();
        int entityDamaged = data.getEntityDamageable();
        float totalDamageTaken = data.getTotalDamageTaken();

//        if (entityDamaged < 10) {
//            texts.get(0).getTextMesh().setString(Integer.toString((int)totalDamageTaken));
//        }
//        else {
//            texts.get(1).getTextMesh().setString(Integer.toString((int)totalDamageTaken));
//
//        }

        //if hitbox comp isnt already deactivated, start visual effect
//        if (!wc.hasComponent(entityDamager, VisualEffectComp.class)) {
//            wc.activateComponent(entityDamager, VisualEffectComp.class);
//        }

        VisualEffectComp visefComp = (VisualEffectComp) wc.getComponent(entityDamager, VisualEffectComp.class);
        PositionComp posComp = (PositionComp) wc.getComponent(entityDamaged, PositionComp.class);

        visefComp.startEffect(0, posComp.getPos());
    }

    private void applyProjectileDead(ProjectileDeadData data) {

        System.out.println("projectile should die: "+data);

        int projOwnerEntity = data.getEntityOwnerId();
        int projAbilityId = data.getProjectileAbilityId();

        for (int projEntity : wc.getEntitiesWithComponentType(ProjectileComp.class)) {
            ProjectileComp projComp = (ProjectileComp)wc.getComponent(projEntity, ProjectileComp.class);
            HitboxComp hitbComp = (HitboxComp)wc.getComponent(projEntity, HitboxComp.class);

            //if projectile does not belong to current character, skip it
            if (hitbComp.getOwner() != projOwnerEntity) continue;

            if (projComp.getAbilityId() == projAbilityId) {
                projComp.setShouldDeactivateFlag();
                break;
            }
        }

    }


    private int readNextPacketId() {
        try {
            return inputStream.readInt();
        }
        catch(IOException e) {e.printStackTrace(); throw new IllegalStateException();}
    }
    private int inBytesAvailable() {
        try {
            return inputStream.available();
        }
        catch(IOException e) {e.printStackTrace(); throw new IllegalStateException("");}
    }

    public AllCharacterStateData readCharacterData() {
        AllCharacterStateData gameState = null;

        int messageBytes = AllCharacterStateData.BYTES;

        if (inBytesAvailable() >= messageBytes) {
            gameState = NetworkUtils.streamToGameState(inputStream);
        }

        return gameState;
    }

    public AbilityStartedData readAbilityStarted() {
        AbilityStartedData data = null;

        int messageBytes = AbilityStartedData.BYTES;

        if (inBytesAvailable() >= messageBytes) {
            data = NetworkUtils.streamToAbilityStarted(inputStream);
        }

        return data;
    }

    public HitDetectedData readHitDetected() {
        HitDetectedData data = null;

        int messageBytes = AbilityStartedData.BYTES;

        if (inBytesAvailable() >= messageBytes) {
            data = NetworkUtils.streamToHitDetected(inputStream);
        }

        return data;
    }


    public ProjectileDeadData readProjectileDead() {
        ProjectileDeadData data = null;

        int messageBytes = ProjectileDeadData.BYTES;

        if (inBytesAvailable() >= messageBytes) {
            data = NetworkUtils.streamToProjectileDead(inputStream);
        }

        return data;
    }
}
