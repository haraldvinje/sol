package engine.network.server;

import engine.network.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;

/**
 * Created by eirik on 21.06.2017.
 */
public class ServerClientHandler {


    private Socket socket;

    private TcpPacketInput tcpPacketIn;
    private TcpPacketOutput tcpPacketOut;

    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    //private Thread inputThread;


    private LinkedList<CharacterInputData> inputData;


    //private LinkedList



    public ServerClientHandler(Socket clientSocket) {

        this.socket = clientSocket;
        try {
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());

            tcpPacketIn = new TcpPacketInput(socket.getInputStream());
            tcpPacketOut = new TcpPacketOutput(socket.getOutputStream());


        } catch (IOException e) {
            e.printStackTrace();
        }

        inputData = new LinkedList<>();

    }

    public TcpPacketInput getTcpPacketIn() {
        return tcpPacketIn;
    }

    public TcpPacketOutput getTcpPacketOut() {
        return tcpPacketOut;
    }

    public void sendEmptyPacket(int packetId){
        tcpPacketOut.sendEmpty(packetId);
    }

    public int available() {
        try {
            return inputStream.available();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean intAvailable(int count) {
        return available() >= Integer.BYTES * count;
    }

    public void sendInt(int i) {
        try {
            outputStream.writeInt(i);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int readInt() {
        try {
            return inputStream.readInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
