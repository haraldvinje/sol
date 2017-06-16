package engine.physics;

import engine.Component;
import utils.maths.Vec2;

/**
 * Created by haraldvinje on 13-Jun-17.
 */
public class PhysicsComp implements Component {

    private Vec2 velocity = new Vec2();
    private Vec2 acceleration = new Vec2();

    private float frictionConstant = 0.1f;



    public void setFrictionConstant(float frictionConstant){
        this.frictionConstant = frictionConstant;
    }

    public void addVelocity(Vec2 velocity) {
        this.velocity = this.velocity.add(velocity);
        //System.out.println("Add physics velocity" + this.velocity);
    }

//    public void setVelocity(Vec2 vector){
//        this.velocity = vector;
//        this.vx = vector.x;
//        this.vy = vector.y;
//    }

    public Vec2 getVelocity(){
        return this.velocity;
    }

    public Vec2 getAcceleration() {
        return acceleration;
    }

//    public void setAcceleration(Vec2 acceleration) {
//        this.acceleration = acceleration;
//    }

    public float getFrictionConst(){
        return this.frictionConstant;
    }

    public void addAcceleration(Vec2 acceleration){
        this.acceleration = this.acceleration.add(acceleration);
    }

    public void resetAcceleration() {
        this.acceleration = new Vec2();
    }
}
