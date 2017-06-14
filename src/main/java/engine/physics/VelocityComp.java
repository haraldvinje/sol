package engine.physics;

import engine.Component;

/**
 * Created by haraldvinje on 13-Jun-17.
 */
public class VelocityComp implements Component{

    private float vx;
    private float vy;

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
}
