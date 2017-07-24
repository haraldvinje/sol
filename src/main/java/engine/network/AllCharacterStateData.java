package engine.network;

import java.util.Arrays;

/**
 * Created by eirik on 21.06.2017.
 */
public class AllCharacterStateData {


    public static final int BYTES = Integer.BYTES + Float.BYTES*3 * NetworkUtils.CHARACTER_NUMB;

    private int frameNumber;

    private float[] x = new float[NetworkUtils.CHARACTER_NUMB],
                    y = new float[NetworkUtils.CHARACTER_NUMB],
                    rotation = new float[NetworkUtils.CHARACTER_NUMB];


    public AllCharacterStateData() {

    }


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

    @Override
    public String toString() {
        return "[AllCharacterStateData: x="+ Arrays.toString(x)+" y="+Arrays.toString(y)+" rotation="+Arrays.toString(rotation)+"]";
    }
}
