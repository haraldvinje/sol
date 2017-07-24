package engine.network;

import utils.maths.M;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketException;

/**
 * Created by eirik on 21.06.2017.
 */
public class NetworkUtils {

    public static final int PORT_NUMBER = 7779;
    public static final int CHARACTER_NUMB = 2;
    //public static final String HOST_NAME

    //network smoothening attributes
    public static int SERVER_INPUT_BUFFERING = 1;
    public static int CLIENT_INPUT_BUFFERING = 2;

    public static float CLIENT_INTERPOLATION_FRAME_COUNT = 1.0f;

    //to simulate packet loss
    public static float PACKET_LOSS_TO_SERVER = 0.0f;
    public static float PACKET_LOSS_TO_CLIENT = 0.0f;

    //packet id's
    public static final int SERVER_CHARACTER_STATE_ID = 0,
            SERVER_ABILITY_STARTED_ID = 1,
            SERVER_HIT_DETECTED_ID = 2,
            SERVER_CHARACTER_DEAD_ID = 3,
            SERVER_PROJECTILE_DEAD_ID = 4;

    /**
     *
     * @param stateData
     * @param out
     * @return false, if reciever is disconnected
     */
    public static boolean gameStateToStream(AllCharacterStateData stateData, DataOutputStream out) {
        //simulate packet loss
        if (PACKET_LOSS_TO_CLIENT > 0) {
            if (M.random() <= PACKET_LOSS_TO_CLIENT) {
                return true;
            }
        }
        try {

            out.writeInt(stateData.getFrameNumber()); //write frame number

            for (int i = 0; i < NetworkUtils.CHARACTER_NUMB; i++) {
                out.writeFloat(stateData.getX(i));
                out.writeFloat(stateData.getY(i));
                out.writeFloat(stateData.getRotation(i));
            }

        } catch (SocketException e) {
            return false;
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("An IO exception that is not a socket exception occured");
        }
        return true;
    }

    public static AllCharacterStateData streamToGameState(DataInputStream in) {
        AllCharacterStateData state = new AllCharacterStateData();

        try {
            state.setFrameNumber(in.readInt()); //read frame number

            for (int i = 0; i < NetworkUtils.CHARACTER_NUMB; i++) {
                state.setX(i, in.readFloat());
                state.setY(i, in.readFloat());
                state.setRotation(i, in.readFloat());
            }


            return state;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException();
    }

    public static boolean characterInputToStream(CharacterInputData charInput, DataOutputStream out) {
        //simulate packet loss
        if (PACKET_LOSS_TO_SERVER > 0) {
            if (M.random() <= PACKET_LOSS_TO_SERVER) {
                return true;
            }
        }
        try {
            out.writeBoolean(charInput.isMoveLeft());
            out.writeBoolean(charInput.isMoveRight());
            out.writeBoolean(charInput.isMoveUp());
            out.writeBoolean(charInput.isMoveDown());

            out.writeBoolean(charInput.isAction1());
            out.writeBoolean(charInput.isAction2());
            out.writeBoolean(charInput.isAction3());

            out.writeFloat(charInput.getAimX());
            out.writeFloat(charInput.getAimY());
        }
        catch (SocketException e) {
            return false;
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static CharacterInputData streamToCharacterInput(DataInputStream in) {
        CharacterInputData id = new CharacterInputData();
        try {
            id.setMovement(in.readBoolean(), in.readBoolean(), in.readBoolean(), in.readBoolean());
            id.setActions(in.readBoolean(), in.readBoolean(), in.readBoolean());
            id.setAim(in.readFloat(), in.readFloat());
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        return id;
    }


    public static boolean abilityStartedDataToStream(DataOutputStream stream, AbilityStartedData data) {
        try {
            stream.writeInt(data.getEntityId());
            stream.writeInt(data.getAbilityId());
        }
        catch (SocketException e) {
            return false;
        }
        catch(IOException e) {e.printStackTrace(); throw new IllegalStateException("");}

        return true;
    }
    public static AbilityStartedData streamToAbilityStarted(DataInputStream stream) {
        AbilityStartedData data = new AbilityStartedData();
        try {
            data.setEntityId( stream.readInt() );
            data.setAbilityId( stream.readInt() );
        }
        catch(IOException e) {e.printStackTrace(); throw new IllegalStateException("");}

        return data;
    }


    public static boolean hitDetectedToStream(DataOutputStream stream, HitDetectedData data) {
        try {
            stream.writeInt(data.getEntityDamager());
            stream.writeInt(data.getEntityDamageable());
            stream.writeFloat(data.getDamageTaken());
        }
        catch (SocketException e) {
            return false;
        }
        catch(IOException e) {e.printStackTrace(); throw new IllegalStateException("");}

        return true;
    }

    public static HitDetectedData streamToHitDetected(DataInputStream stream) {
        HitDetectedData data = new HitDetectedData();
        try {
            data.setEntityDamager( stream.readInt() );
            data.setEntityDamageable( stream.readInt() );
            data.setDamageTaken( stream.readFloat() );
        }
        catch(IOException e) {e.printStackTrace(); throw new IllegalStateException("");}

        return data;
    }


    public static boolean projectileDeadToStream(DataOutputStream stream, ProjectileDeadData data) {
        try {
            stream.writeInt(data.getEntityOwnerId());
            stream.writeInt(data.getProjectileAbilityId());
        }
        catch (SocketException e) {
            return false;
        }
        catch(IOException e) {e.printStackTrace(); throw new IllegalStateException("");}

        return true;
    }

    public static ProjectileDeadData streamToProjectileDead(DataInputStream stream) {
        ProjectileDeadData data = new ProjectileDeadData();
        try {
            data.setEntityOwnerId( stream.readInt() );
            data.setProjectileAbilityId( stream.readInt() );
        }
        catch(IOException e) {e.printStackTrace(); throw new IllegalStateException("");}

        return data;
    }




    public static boolean sendClientStateId(int id, DataOutputStream outputStream) {

        try{
            System.out.println("Sending id: " + id + " to " + outputStream.toString());
            outputStream.write(id);
        }


        catch (SocketException e) {
            return false;
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return true;

    }

    public static int streamToCharacterSelected(DataInputStream inputStream) {
        int characterId = -1;
        try{
            characterId = inputStream.readInt();
        }
        catch (IOException e){
            System.out.println("Could not read");
        }
        return characterId;
    }

}
