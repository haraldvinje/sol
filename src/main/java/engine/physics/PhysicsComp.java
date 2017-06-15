package engine.physics;

import engine.Component;
import engine.maths.Vec2;

/**
 * Created by haraldvinje on 13-Jun-17.
 */
public class PhysicsComp implements Component {

    private float vx;
    private float vy;
    private Vec2 velocity;

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

    public void addVelocity(Vec2 velocity) {
        vx = vx + velocity.x;
        vy = vy + velocity.y;
    }

    public void setVelocity(Vec2 vector){
        this.velocity = vector;
        this.vx = vector.x;
        this.vy = vector.y;
    }

    public Vec2 getVelocity(){
        return this.velocity;
    }
}
