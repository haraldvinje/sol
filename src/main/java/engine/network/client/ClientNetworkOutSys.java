package engine.network.client;

import engine.Sys;
import engine.UserInput;
import engine.WorldContainer;
import engine.network.CharacterInputData;
import engine.network.NetworkUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by eirik on 04.07.2017.
 */
public class ClientNetworkOutSys implements Sys {


    private WorldContainer wc;

    private DataOutputStream outputStream;

    private UserInput userInput;


    public ClientNetworkOutSys(Socket socket, UserInput userInput) {
        this.userInput = userInput;

        try {
            outputStream = new DataOutputStream(socket.getOutputStream());
        }
        catch (IOException e) {
            e.printStackTrace();
            System.err.println("Could not get output stream from socket");
        }

    }

    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.wc = wc;
    }


    @Override
    public void update() {
        updateServerByInput();
    }

    private void updateServerByInput() {

        CharacterInputData input = retrieveUserInput();
        sendInputData(input);
    }

    /**
     * retrieve input straight from user as of now
     * @return
     */
    public CharacterInputData retrieveUserInput() {
        CharacterInputData input = new CharacterInputData();

        input.setMoveLeft(userInput.isKeyboardPressed(UserInput.KEY_A));
        input.setMoveRight(userInput.isKeyboardPressed(UserInput.KEY_D));
        input.setMoveUp(userInput.isKeyboardPressed(UserInput.KEY_W));
        input.setMoveDown(userInput.isKeyboardPressed(UserInput.KEY_S));

        input.setAction1(userInput.isMousePressed(UserInput.MOUSE_BUTTON_1));
        input.setAction2(userInput.isMousePressed(UserInput.MOUSE_BUTTON_2));
        input.setAction3(userInput.isKeyboardPressed(UserInput.KEY_SPACE));

        input.setAimX(userInput.getMouseX());
        input.setAimY(userInput.getMouseY());

        return input;
    }

    public void sendInputData(CharacterInputData id) {

        NetworkUtils.characterInputToStream(id, outputStream); //protocol
    }


    @Override
    public void terminate() {
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
