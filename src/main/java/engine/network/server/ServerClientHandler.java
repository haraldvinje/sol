package engine.network.server;

import engine.network.CharacterInputData;
import engine.network.GameStateData;

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
    public boolean sendStateData(GameStateData stateData) {
        //System.out.println("[server] sending state data");
        try {
            DataOutputStream out = outputStream;
            out.writeFloat(stateData.getX1());
            out.writeFloat(stateData.getY1());
            out.writeFloat(stateData.getRotation1());

            out.writeFloat(stateData.getX2());
            out.writeFloat(stateData.getY2());
            out.writeFloat(stateData.getRotation2());

        } catch (SocketException e) {
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("An IO exception that is not a socket exception occured");
        }
        return true;
    }

    public CharacterInputData getInputData() {
        try {
            int availableInput = inputStream.available();
            if (availableInput >= CharacterInputData.BYTES) {

                return readInputBytes(inputStream);

            }
            else {
                System.err.println("Not enough input for a inputState, numb of bytes ready: " + availableInput);
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("IO exception");
        }

    }

    private CharacterInputData readInputBytes(DataInputStream is) {
        CharacterInputData id = new CharacterInputData();
        try {
            id.setMovement(is.readBoolean(), is.readBoolean(), is.readBoolean(), is.readBoolean());
            id.setActions(is.readBoolean(), is.readBoolean());
            id.setAim(is.readFloat(), is.readFloat());
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        return id;
    }

}
