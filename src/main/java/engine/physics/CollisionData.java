package engine.physics;

import engine.WorldContainer;
import utils.maths.Vec2;

/**
 * Created by haraldvinje on 14-Jun-17.
 */
public class CollisionData {


    private int entity1, entity2;
    private Vec2 collisionVector;
    private float penetrationDepth;



    public CollisionData(int e1, int e2){
        this.entity1 = e1;
        this.entity2 = e2;
    }

    public int getEntity1() {
        return entity1;
    }

    public int getEntity2() {
        return entity2;
    }

    public void setCollisionVector(Vec2 vector){this.collisionVector = vector; }

    public void setPenetrationDepth(float penetrationDepth) {this.penetrationDepth = penetrationDepth; }

    public Vec2 getCollisionVector() {return collisionVector; }

    public float getPenetrationDepth() {return penetrationDepth;
    }


}
