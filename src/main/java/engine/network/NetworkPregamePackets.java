package engine.network;

/**
 * Created by eirik on 27.07.2017.
 */
public class NetworkPregamePackets {

    public static final int
            //packet ids in states        CLIENT/SERVER is the program a packet is sendt from

            QUEUE_CLIENT_REQUEST_QUEUE = 0,
            QUEUE_SERVER_PUT_IN_QUEUE = 1,
            QUEUE_SERVER_GOTO_CHARACTERSELECT = 2,


            CHARSELECT_CLIENT_CHOSE_CHARACTER = 3,
            CHARSELECT_SERVER_GOTO_GAME = 4,

            INGAME_CLIENT_READY = 5,
            INGAME_SERVER_CLIENT_GAME_TEAMS = 6


//            //packet sizes
//            CHARSELECT_SERVER_GOTO_GAME_SIZE = 7 * Integer.BYTES;

                    ;
}
