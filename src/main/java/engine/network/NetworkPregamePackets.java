package engine.network;

/**
 * Created by eirik on 27.07.2017.
 */
public class NetworkPregamePackets {

    public static final int
            //packet ids in states        CLIENT/SERVER is the program a packet is sendt from

            QUEUE_CLIENT_REQUEST_QUEUE = 30,
            QUEUE_SERVER_PUT_IN_QUEUE = 31,
            QUEUE_SERVER_GOTO_CHARACTERSELECT = 32,


            CHARSELECT_CLIENT_CHOSE_CHARACTER = 33,
            CHARSELECT_SERVER_GOTO_GAME = 34,

            INGAME_CLIENT_READY = 35,
            INGAME_SERVER_CLIENT_GAME_TEAMS = 36


//            //packet sizes
//            CHARSELECT_SERVER_GOTO_GAME_SIZE = 7 * Integer.BYTES;

                    ;
}
