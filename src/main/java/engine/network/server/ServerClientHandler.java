package engine.network.server;

import engine.network.CharacterInputData;
import engine.network.GameStateData;
import engine.network.NetworkUtils;

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
        return NetworkUtils.gameStateToStream(stateData, outputStream);
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
