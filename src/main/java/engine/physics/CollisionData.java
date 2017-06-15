package engine.physics;

import engine.maths.Vec2;

/**
 * Created by haraldvinje on 14-Jun-17.
 */
public class CollisionData {

    private CollisionComp c1, c2;
    private Vec2 collisionVector;
    private float penetrationDepth;

    public CollisionComp getCollisionComp1() {
        return c1;
    }


    public CollisionComp getCollisionComp2() {
        return c2;
    }


    public CollisionData(CollisionComp c1, CollisionComp c2){
        this.c1 = c1;
        this.c2 = c2;

    }

    public void setCollisionVector(Vec2 vector){this.collisionVector = vector; }

    public void setPenetrationDepth(float penetrationDepth) {this.penetrationDepth = penetrationDepth; }

    public Vec2 getCollisionVector() {return collisionVector; }

    public float getPenetrationDepth() {return penetrationDepth;
    }


}
