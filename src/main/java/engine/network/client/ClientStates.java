package engine.network.client;

/**
 * Created by eirik on 04.07.2017.
 */
public enum ClientStates {
    INTRO, CONNECTING, IDLE, WAITING_GAME, CHOOSING_CHARACTER, INGAME;

    private static int size = ClientStates.values().length;
    public static int size() {
        return size;
    }
}
