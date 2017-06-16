package engine;

import utils.maths.Vec2;

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
    public void addX(float x) {
        setX(getX()+x);
    }

    public float getY() {
        return y;
    }
    public void setY(float y) {
        this.y = y;
    }
    public void addY(float y) {
        setY(getY()+y);
    }

    public void setPos(Vec2 v) {
        setX(v.x);
        setY(v.y);
    }
    public void addPos(Vec2 vector){
        addX(vector.x);
        addY(vector.y);
    }
    public Vec2 getPos() {
        return new Vec2(x, y);
    }


//    @Override
//    public int getMask() {
//        return WorldContainer.COMPMASK_POSITION;
//    }
}
