package engine.network.client.clientStates;

import engine.PositionComp;
import engine.WorldContainer;
import engine.graphics.ColoredMeshComp;
import engine.graphics.ColoredMeshUtils;
import engine.graphics.MeshCenterComp;
import engine.graphics.ViewRenderComp;
import engine.graphics.text.Font;
import engine.graphics.text.TextMesh;
import engine.network.*;
import engine.network.client.*;
import utils.maths.Vec4;

/**
 * Created by eirik on 04.07.2017.
 */
public class ClientWaitingState extends ClientState {

    private boolean inServerQueue;


    private int queueTextEntity;
    private String queueString = "In queue";


//    private boolean ready = false;
//    private int nextMessageId;



    @Override
    public void init() {
        super.init();

        createInitialEntities(wc);
    }

    @Override
    public void onEnter() {


//        nextMessageId = -1;
        inServerQueue = false;

        //tell server that we want to go into queue
        sendGotoQueue();
    }

    @Override
    public void onUpdate() {
//        NetworkDataInput in = client.getNetInStream();
//
//        //check if we are waiting for new message and we have a new message id pending
//        if (nextMessageId == -1 && in.intAvailable() >= 2) {
//            int packetId = in.readInt(); //should be the current state the client is in
//
//            //check if first int of message corresponds to the current client state
//            if (packetId != NetworkPregamePackets.QUEUE_ID) {
//                throw new IllegalStateException(
//                        "Got wrong packet id in client waiting state.\n" +
//                        "Got: " + packetId + " expected: " + NetworkPregamePackets.QUEUE_ID);
//            }
//
//            //read the meassage id
//            nextMessageId = in.readInt();
//        }

        //handle incoming data
//        handleNetInput(in);

        TcpPacketInput in = client.getTcpPacketIn();

        //handle if client is put in queue
        if (in.removeIfHasPacket(NetworkPregamePackets.QUEUE_SERVER_PUT_IN_QUEUE)) {
            inServerQueue = true;
            ClientUtils.setEntityString(wc, queueTextEntity, queueString);
        }

        if (in.removeIfHasPacket(NetworkPregamePackets.QUEUE_SERVER_GOTO_CHARACTERSELECT)) {
            setGotoState(ClientStates.CHOOSING_CHARACTER);
        }

    }


    @Override
    public void onExit() {

    }

//    private void handleNetInput(NetworkDataInput in) {
//        if (nextMessageId == -1) return;
//
//        switch(nextMessageId) {
//            case NetworkPregamePackets.QUEUE_SERVER_PUT_IN_QUEUE:
//                //put in queue
//
//                //no more data to read, so whole packet has arrived
//
//                //tell that we have been put in queue on server
//                inServerQueue = true;
//                ClientUtils.setEntityString(wc, queueTextEntity, queueString);
//
//                //tell that we are finished with this packet type
//                nextMessageId = -1;
//
//                break;
//
//            case NetworkPregamePackets.QUEUE_SERVER_GOTO_CHARACTERSELECT:
//                //game started
//
//                //no data to read
//
//                //tell that we are finished with this packet
//                nextMessageId = -1;
//
//                //goto characterselect
//                setGotoState(ClientStates.CHOOSING_CHARACTER);
//
//                break;
//        }
//    }

    private boolean sendGotoQueue() {
        //send data corresponding to say that we want to be put in queue

        //send packet with only id
        client.getTcpPacketOut().sendEmpty(NetworkPregamePackets.QUEUE_CLIENT_REQUEST_QUEUE);

        //should check if server is disconnected
        return true;
    }


    private void createInitialEntities(WorldContainer wc) {
        float width = Client.CLIENT_WIDTH/2;
        float height = Client.CLIENT_HEIGHT/6;

        int rect = wc.createEntity();
        wc.addComponent(rect, new PositionComp(Client.CLIENT_WIDTH/2f, Client.CLIENT_HEIGHT/2f));
        wc.addComponent(rect, new ColoredMeshComp(ColoredMeshUtils.createRectangle(width, height) ));
        wc.addComponent(rect, new MeshCenterComp(width/2f, height/2f));


        //create queue text
        queueTextEntity = wc.createEntity();
        wc.addComponent(queueTextEntity, new PositionComp(400, 100));
        wc.addComponent(queueTextEntity, new ViewRenderComp(new TextMesh("", Font.getDefaultFont(), 52, new Vec4(1,1,1,1))));
    }
}
