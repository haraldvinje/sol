import engine.network.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import utils.maths.M;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by eirik on 07.07.2017.
 */
public class NetworkProtocolTest {

    private int port = 7777;

    private ServerSocket serverAcceptSocket;
    private Socket serverSocket, clientSocket;

    private DataOutputStream serverOut, clientOut;
    private DataInputStream serverIn, clientIn;

    @Before
    public void setUp() {

        try {
            serverAcceptSocket = new ServerSocket(port);

            Thread serverAcceptThread = new Thread( () -> {
                try {
                    serverSocket = serverAcceptSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } );
            serverAcceptThread.start();


            Thread.sleep(100);

            clientSocket = new Socket("localhost", port);

            Thread.sleep(100);

            serverAcceptThread.join();


            serverOut = new DataOutputStream( serverSocket.getOutputStream() );
            serverIn = new DataInputStream( serverSocket.getInputStream() );

            clientOut = new DataOutputStream( clientSocket.getOutputStream() );
            clientIn = new DataInputStream( clientSocket.getInputStream() );

        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testConnection() {
        int serverSend = 4536;
        int clientSend = 965432;

        int serverReceive = 0;
        int clientReceive = 0;

        try {
            serverOut.writeInt(serverSend);
            clientOut.writeInt(clientSend);

            Thread.sleep(100);

            clientReceive = clientIn.readInt();
            serverReceive = serverIn.readInt();

        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }


        Assert.assertEquals(serverSend, clientReceive);
        Assert.assertEquals(clientSend, serverReceive);
    }

    @Test
    public void testCharacterState() {
        try {

            AllCharacterStateData data = new AllCharacterStateData();
            data.setFrameNumber(10);
            for (int i = 0; i < NetworkUtils.CHARACTER_NUMB; i++) {
                data.setX(i, largeRandom());
                data.setY(i, largeRandom());
                data.setRotation(i, largeRandom());
            }

            NetworkUtils.gameStateToStream(data, serverOut);

            Thread.sleep(100);

            AllCharacterStateData receivedData = NetworkUtils.streamToGameState(clientIn);

            System.out.println(data);
            System.out.println(receivedData);
            Assert.assertEquals(data.toString(), receivedData.toString());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    @Test
    public void testAbilityStarted() {

        try {

            AbilityStartedData data = new AbilityStartedData();
            data.setAbilityId(largeRandomInt());
            data.setEntityId(largeRandomInt());

            NetworkUtils.abilityStartedDataToStream(serverOut, data);

            Thread.sleep(100);

            AbilityStartedData receivedData = NetworkUtils.streamToAbilityStarted(clientIn);

            System.out.println(data);
            System.out.println(receivedData);
            Assert.assertEquals(data.toString(), receivedData.toString());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testHitDetected() {
        try {

            HitDetectedData data = new HitDetectedData();
            data.setEntityHit(largeRandomInt());
            data.setTotalDamageTaken(largeRandom());

            NetworkUtils.hitDetectedToStream(serverOut, data);

            Thread.sleep(100);

            HitDetectedData receivedData = NetworkUtils.streamToHitDetected(clientIn);

            System.out.println(data);
            System.out.println(receivedData);
            Assert.assertEquals(data.toString(), receivedData.toString());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        try {
            clientSocket.close();

            serverAcceptSocket.close();
            serverSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private float largeRandom() {
        return M.random() * Integer.MAX_VALUE;
    }
    private int largeRandomInt() {
        return (int)( M.random() * Integer.MAX_VALUE );
    }
}
