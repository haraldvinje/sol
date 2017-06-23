package engine.network;

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


    public static boolean gameStateToStream(GameStateData stateData, DataOutputStream out) {
        try {
            for (int i = 0; i < NetworkUtils.CHARACTER_NUMB; i++) {
                out.writeFloat(stateData.getX(i));
                out.writeFloat(stateData.getY(i));
                out.writeFloat(stateData.getRotation(i));
                out.writeInt(stateData.getAbilityExecuted(i));
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

    public static GameStateData streamToGameState(DataInputStream in) {
        GameStateData state = new GameStateData();

        try {
            for (int i = 0; i < NetworkUtils.CHARACTER_NUMB; i++) {
                state.setX(i, in.readFloat());
                state.setY(i, in.readFloat());
                state.setRotation(i, in.readFloat());
                state.setAbilityExecuted(i, in.readInt()); //<------------------------- No sbility
            }


            return state;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException();
    }

    public static boolean characterInputToStream(CharacterInputData charInput, DataOutputStream out) {
        try {
            out.writeBoolean(charInput.isMoveLeft());
            out.writeBoolean(charInput.isMoveRight());
            out.writeBoolean(charInput.isMoveUp());
            out.writeBoolean(charInput.isMoveDown());

            out.writeBoolean(charInput.isAction1());
            out.writeBoolean(charInput.isAction2());

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
            id.setActions(in.readBoolean(), in.readBoolean());
            id.setAim(in.readFloat(), in.readFloat());
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        return id;
    }


}
