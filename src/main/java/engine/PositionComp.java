package engine;

import engine.maths.Vec2;

/**
 * Created by eirik on 13.06.2017.
 */
public class PositionComp implements Component {

    private float x, y;

    public PositionComp(float x, float y) {
        setX(x);
        setY(y);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void addVector(Vec2 vector){
        setY(y+vector.);
    }


//    @Override
//    public int getMask() {
//        return WorldContainer.COMPMASK_POSITION;
//    }
}
