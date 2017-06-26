package engine.network.server;

import engine.PositionComp;
import engine.RotationComp;
import engine.Sys;
import engine.WorldContainer;
import engine.character.CharacterComp;
import engine.character.CharacterInputComp;
import engine.graphics.ColoredMeshComp;
import engine.graphics.ColoredMeshUtils;
import engine.network.CharacterInputData;
import engine.network.GameStateData;
import engine.network.NetworkUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 * Created by eirik on 21.06.2017.
 */
public class ServerNetworkSys implements Sys {

    private WorldContainer wc;


    private ServerConnectionInput connectionInput;
    private Thread serverConnectionInputThread;

    private List<ServerClientHandler> clientHandlers;


    public ServerNetworkSys() {

        connectionInput = new ServerConnectionInput(NetworkUtils.PORT_NUMBER);
        serverConnectionInputThread = new Thread(connectionInput);

        clientHandlers = new ArrayList<>();


        serverConnectionInputThread.start();
    }


    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.wc = wc;
    }

    @Override
    public void update() {

        checkNewConnections();

        updateCharactersByInput();

        updateClientsByGameState();
    }

    @Override
    public void terminate() {
        connectionInput.terminate();


        try {
            serverConnectionInputThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void checkNewConnections() {
        if (connectionInput.hasConnectedClients()) {
            System.out.println("Retrieving new connection, connections= "+clientHandlers.size());
            ServerClientHandler clientHandler = connectionInput.getConnectedClient();

            clientHandlers.add(clientHandler);

            createClientIcon();
        }
    }

    private void updateCharactersByInput() {
        //update each character. WATCH THE ORDERING OF CHARACTERS CORRESPONDING TO CLIENTS
        //This should be done at the start of iteration.. if feels necessary...
        int i = 0;
        for (int entity : wc.getEntitiesWithComponentType(CharacterComp.class)) {
            if (clientHandlers.size() == 0 || (clientHandlers.size() == 1 && i==1) ) break; //<-----------------------------------------------UGLY

            //get input from each client, hoply corresponding to character ordering, and update the entity
            CharacterInputComp inpComp = (CharacterInputComp) wc.getComponent(entity, CharacterInputComp.class);
            CharacterInputData inData = clientHandlers.get(i).getInputData();

            if (inData != null) {
                //System.out.println("Got input data: "+inData);
                writeInDataToComp(inData, inpComp);
            }

            i++;
        }
    }

    private void updateClientsByGameState() {
        //get game state
        GameStateData stateData = retrieveGameState();

        System.out.println("Sending game state: " + stateData);

        //send game state
        sendGameState(stateData);
    }


    private GameStateData retrieveGameState() {
        GameStateData sd = new GameStateData();

        Set<Integer> chars = wc.getEntitiesWithComponentType(CharacterComp.class);
        if (chars.size() != 2) throw new IllegalStateException("THere is not 2 characters on the field :(");

        int charNumb = 0;
        for (int c : chars) {
            PositionComp posComp = (PositionComp)wc.getComponent(c, PositionComp.class);
            RotationComp rotComp = (RotationComp)wc.getComponent(c, RotationComp.class);

            CharacterComp charComp = (CharacterComp)wc.getComponent(c, CharacterComp.class);
            //add characters created

            sd.setX(charNumb, posComp.getX());
            sd.setY(charNumb, posComp.getY());
            sd.setRotation(charNumb, rotComp.getAngle());
            if (charComp.shootExecuted) {
                sd.setAbilityExecuted(charNumb, 1);
                charComp.shootExecuted = false;
            }

            charNumb++;
        }

        return sd;
    }


    public void sendGameState(GameStateData gameState) {
        ListIterator<ServerClientHandler> it = clientHandlers.listIterator();
        while (it.hasNext()) {
            ServerClientHandler handler = it.next();

            if (!handler.sendStateData(gameState) ) {
                //client has disconnected, remove it

                it.remove();
            }
        }
    }



    /**
     * Cannot be removed as of now
     */
    private void createClientIcon() {
        float startX = 100, startY = 100;
        float iconRadius = 64;
        int e = wc.createEntity();
        wc.addComponent(e, new PositionComp(startX+clientHandlers.size()*iconRadius*2,   startY));
        wc.addComponent(e, new ColoredMeshComp(ColoredMeshUtils.createCircleTwocolor(iconRadius, 9)));

    }


    private void writeInDataToComp(CharacterInputData inData, CharacterInputComp inpComp) {
        inpComp.setMoveLeft( inData.isMoveLeft() );
        inpComp.setMoveRight( inData.isMoveRight() );
        inpComp.setMoveUp( inData.isMoveUp() );
        inpComp.setMoveDown( inData.isMoveDown() );

        inpComp.setAction1( inData.isAction1() );
        inpComp.setAction2( inData.isAction2() );

        inpComp.setAimX( inData.getAimX() );
        inpComp.setAimY( inData.getAimY() );
    }


}
