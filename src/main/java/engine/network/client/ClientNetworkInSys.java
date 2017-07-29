package engine.network.client;

import engine.PositionComp;
import engine.Sys;
import engine.WorldContainer;
import engine.audio.AudioComp;
import engine.character.CharacterComp;
import engine.combat.DamageableComp;
import engine.combat.abilities.AbilityComp;
import engine.combat.abilities.HitboxComp;
import engine.combat.abilities.ProjectileComp;
import engine.network.*;
import engine.network.networkPackets.AbilityStartedData;
import engine.network.networkPackets.AllCharacterStateData;
import engine.network.networkPackets.HitDetectedData;
import engine.visualEffect.VisualEffectComp;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by eirik on 04.07.2017.
 */
public class ClientNetworkInSys implements Sys{



    private WorldContainer wc;

    private TcpPacketInput tcpPacketIn;


    private LinkedList<AllCharacterStateData> statesPending = new LinkedList<>();


    public ClientNetworkInSys(TcpPacketInput tcpPacketIn) {

        this.tcpPacketIn = tcpPacketIn;

    }

    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.wc = wc;

    }

    @Override
    public void update() {
        //poll net in
        tcpPacketIn.pollPackets();

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

        //retrieve new packets, they are null if no packet available

        LinkedList<NetworkDataInput> characterStateData = tcpPacketIn.pollAllPackets(NetworkUtils.SERVER_CHARACTER_STATE_ID);
        NetworkDataInput abilityStartedData = tcpPacketIn.pollPacket(NetworkUtils.SERVER_ABILITY_STARTED_ID);
        NetworkDataInput hitDetectedData = tcpPacketIn.pollPacket(NetworkUtils.SERVER_HIT_DETECTED_ID);
        NetworkDataInput projectileDeadData = tcpPacketIn.pollPacket(NetworkUtils.SERVER_PROJECTILE_DEAD_ID);



        //for each state data packet, might be 0
        for (NetworkDataInput data : characterStateData) {
            //translate packet
            AllCharacterStateData stateData = NetworkUtils.packetToGameState(data);

            //add packet to pendingPackets
            statesPending.add(stateData);
        }

        if (abilityStartedData != null) {
            //translate data
            AbilityStartedData data = NetworkUtils.packetToAbilityStarted(abilityStartedData);

            //Handle
            applyAbilityStarted(data);
        }

        if (hitDetectedData != null) {
            //translate
            HitDetectedData data = NetworkUtils.packetToHitDetected(hitDetectedData);

            //handle
            applyHitDetected(data);
        }

        if (projectileDeadData != null) {
            //translate
            ProjectileDeadData data = NetworkUtils.packetToProjectileDead(projectileDeadData);

            //handle
            applyProjectileDead(data);
        }

        if (!statesPending.isEmpty()) {
            System.out.println("states pending: " + statesPending.size());
            //apply last state obtained
            while(statesPending.size() > 1) {
                statesPending.poll();
            }
            applyCharacterStates(statesPending.poll());

//            //remove too old states
//            int statesPendingCount = statesPending.size();
//            for (int i = 1 + NetworkUtils.CLIENT_INPUT_BUFFERING; i < statesPendingCount; i++) {
//                statesPending.poll();
//            }
//
//            //apply state
//            applyCharacterStates(statesPending.poll());
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
        float damageTaken = data.getDamageTaken();

        PositionComp dmgablPosComp = (PositionComp) wc.getComponent(entityDamaged, PositionComp.class);
        DamageableComp dmgableComp = (DamageableComp) wc.getComponent(entityDamaged, DamageableComp.class);

        VisualEffectComp dmgerVisefComp = (VisualEffectComp) wc.getComponent(entityDamager, VisualEffectComp.class);

        dmgableComp.applyDamage(damageTaken);

        dmgerVisefComp.startEffect(0, dmgablPosComp.getPos());

        if (wc.hasComponent(entityDamager, AudioComp.class)) {
            AudioComp dmgerAudioComp = (AudioComp) wc.getComponent(entityDamager, AudioComp.class);

            if (dmgerAudioComp.hasSound(0)) {
                dmgerAudioComp.playSound(0);
            }
        }
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



//    public AllCharacterStateData readCharacterData() {
//        AllCharacterStateData gameState = null;
//
//        int messageBytes = AllCharacterStateData.BYTES;
//
//        if (inBytesAvailable() >= messageBytes) {
//            gameState = NetworkUtils.streamToGameState(inputStream);
//        }
//
//        return gameState;
//    }
//
//    public AbilityStartedData readAbilityStarted() {
//        AbilityStartedData data = null;
//
//        int messageBytes = AbilityStartedData.BYTES;
//
//        if (inBytesAvailable() >= messageBytes) {
//            data = NetworkUtils.streamToAbilityStarted(inputStream);
//        }
//
//        return data;
//    }
//
//    public HitDetectedData readHitDetected() {
//        HitDetectedData data = null;
//
//        int messageBytes = AbilityStartedData.BYTES;
//
//        if (inBytesAvailable() >= messageBytes) {
//            data = NetworkUtils.streamToHitDetected(inputStream);
//        }
//
//        return data;
//    }
//
//
//    public ProjectileDeadData readProjectileDead() {
//        ProjectileDeadData data = null;
//
//        int messageBytes = ProjectileDeadData.BYTES;
//
//        if (inBytesAvailable() >= messageBytes) {
//            data = NetworkUtils.streamToProjectileDead(inputStream);
//        }
//
//        return data;
//    }
}
