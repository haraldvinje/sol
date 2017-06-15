package engine.physics;

import engine.Component;
import engine.maths.Vec2;

/**
 * Created by haraldvinje on 13-Jun-17.
 */
public class VelocityComp implements Component {

    private float vx;
    private float vy;
    private Vec2 vector;

    public float getVx() {
        return vx;
    }

    public void setVx(float vx) {
        this.vx = vx;
    }

    public float getVy() {
        return vy;
    }

    public void setVy(float vy) {
        this.vy = vy;
    }

    public void addVector(Vec2 vector) {
        vx = vx + vector.x;
        vy = vy + vector.y;
    }

    public void setVector(Vec2 vector){
        this.vector = vector;
        this.vx = vector.x;
        this.vy = vector.y;
    }

    public Vec2 getVector(){
        return this.vector;
    }
}
