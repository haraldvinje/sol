package engine.network;

/**
 * Created by eirik on 21.06.2017.
 */
public class GameStateData {


    public static final int BYTES = Float.BYTES*6;

    private float x1, y1, rotation1;
    private float x2, y2, rotation2;



    public float getX1() {
        return x1;
    }

    public void setX1(float x1) {
        this.x1 = x1;
    }

    public float getY1() {
        return y1;
    }

    public void setY1(float y1) {
        this.y1 = y1;
    }

    public float getRotation1() {
        return rotation1;
    }

    public void setRotation1(float rotation1) {
        this.rotation1 = rotation1;
    }

    public float getX2() {
        return x2;
    }

    public void setX2(float x2) {
        this.x2 = x2;
    }

    public float getY2() {
        return y2;
    }

    public void setY2(float y2) {
        this.y2 = y2;
    }

    public float getRotation2() {
        return rotation2;
    }

    public void setRotation2(float rotation2) {
        this.rotation2 = rotation2;
    }

    @Override
    public String toString() {
        return "[GameStateData: x1="+x1+" y1="+y1+" rotation1="+rotation1+" x2="+x2+" y2="+y2+" rotation2="+rotation2+"]";
    }
}
