package engine.network.networkPackets;

/**
 * Created by eirik on 31.07.2017.
 */
public class GameOverData {

    public static final int BYTES = Integer.BYTES;


    public int charEntityLost;


    public GameOverData(int charEntityLost) {
        this.charEntityLost = charEntityLost;
    }

    public GameOverData() {
    }
}
