package engine.network;

import java.util.Arrays;

/**
 * Created by eirik on 21.06.2017.
 */
public class GameStateData {


    public static final int BYTES = Integer.BYTES + Float.BYTES*6 + Integer.BYTES*2 *2;

    private int frameNumber;

    private float[] x = new float[NetworkUtils.CHARACTER_NUMB],
                    y = new float[NetworkUtils.CHARACTER_NUMB],
                    rotation = new float[NetworkUtils.CHARACTER_NUMB];
    private int[] abilityExecuted = new int[NetworkUtils.CHARACTER_NUMB];
    private int[] abilityTerminated = new int[NetworkUtils.CHARACTER_NUMB];


    public int getFrameNumber() {
        return frameNumber;
    }
    public void setFrameNumber(int frameNumber) {
        this.frameNumber = frameNumber;
    }

    public float getX(int charNumb) {
        return x[charNumb];
    }

    public void setX(int charNumb, float x) {
        this.x[charNumb] = x;
    }

    public float getY(int charNumb) {
        return y[charNumb];
    }

    public void setY(int charNumb, float y) {
        this.y[charNumb] = y;
    }

    public float getRotation(int charNumb) {
        return rotation[charNumb];
    }

    public void setRotation(int charNumb, float rotation) {
        this.rotation[charNumb] = rotation;
    }

    public int getAbilityExecuted(int charNumb) {
        return abilityExecuted[charNumb];
    }
    public void setAbilityExecuted(int charNumb, int ability) {
        abilityExecuted[charNumb] = ability;
    }

    public int getAbilityTerminated(int charNumb) {
        return abilityTerminated[charNumb];
    }
    public void setAbilityTerminated(int charNumb, int ability) {
        this.abilityTerminated[charNumb] = ability;
    }

    @Override
    public String toString() {
        return "[GameStateData: x="+ Arrays.toString(x)+" y="+Arrays.toString(y)+" rotation="+Arrays.toString(rotation)+"]";
    }
}
