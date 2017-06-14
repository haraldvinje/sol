package engine;

import engine.maths.Vec2;

/**
 * Created by haraldvinje on 14-Jun-17.
 */
public class CollisionData {

    private Vec2 collisionVector;
    private float penetrationDepth;

    public CollisionData(CollisionComp c1, CollisionComp c2){

    }

    public Vec2 getCollisionVector() {
        return collisionVector;
    }

    public float getPenetrationDepth() {
        return penetrationDepth;
    }


}
