package engine.network.server;

import engine.network.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

/**
 * Created by eirik on 21.06.2017.
 */
public class ServerClientHandler {


    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    //private Thread inputThread;


    private LinkedList<CharacterInputData> inputData;


    //private LinkedList


    public void sendInt(int i) {
        try {
            outputStream.writeInt(i);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ServerClientHandler(Socket clientSocket) {

        this.socket = clientSocket;
        try {
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }

        inputData = new LinkedList<>();

//        //set up recieve thread
//        inputThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }

    }

    /**
     *
     * @param stateData
     * @return false if socket is disconnected, true otherwise
     */
    public boolean sendCharacterData(AllCharacterStateData stateData) {
        try {
            outputStream.writeInt(NetworkUtils.SERVER_CHARACTER_STATE_ID);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return NetworkUtils.gameStateToStream(stateData, outputStream);
    }
    boolean sendAbilityStarted(AbilityStartedData abData) {
        try {
            outputStream.writeInt(NetworkUtils.SERVER_ABILITY_STARTED_ID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return NetworkUtils.abilityStartedDataToStream(outputStream, abData);
    }
    boolean sendHitDetected(HitDetectedData hitData) {
        try {
            outputStream.writeInt(NetworkUtils.SERVER_HIT_DETECTED_ID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return NetworkUtils.hitDetectedToStream(outputStream, hitData);
    }
    boolean sendProjectileDead(ProjectileDeadData projDeadData) {
        try {
            outputStream.writeInt(NetworkUtils.SERVER_PROJECTILE_DEAD_ID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return NetworkUtils.projectileDeadToStream(outputStream, projDeadData);
    }




    public boolean sendClientStateId(int id){
        try{
            System.out.println("Sending id: " + id + " to " + outputStream.toString());
            outputStream.writeInt(id);
        }


        catch (SocketException e) {
            return false;
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return true;

    }


    public int getCharacterSelectedData(){
        int characterSelected = -1;
        try {
            if (inputStream.available()>=1){
                characterSelected = inputStream.readInt();
            }

        }
        catch (IOException e){
            System.out.println("IO Exception occured");
        }
        return characterSelected;
    }



    public CharacterInputData getInputData() {
        try {
            int messageBytes = CharacterInputData.BYTES;

            if (inputStream.available() >= messageBytes) {

                //remove delayed data
                while (inputStream.available() >= messageBytes*(2 + NetworkUtils.SERVER_INPUT_BUFFERING) ) {
                    inputStream.skipBytes(messageBytes);
                }

                return NetworkUtils.streamToCharacterInput(inputStream);

            }
            else {
                //System.err.println("Not enough input for a inputState, numb of bytes ready: " + inputStream.available());
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("IO exception");
        }

    }


}
