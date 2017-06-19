package engine.physics;

import engine.PositionComp;
import engine.WorldContainer;
import javafx.geometry.Pos;
import utils.maths.Vec2;

/**
 * Created by haraldvinje on 14-Jun-17.
 */
public class CollisionData {


    private int entity1, entity2;
    private CollisionComp collComp1, collComp2;
    private PhysicsComp physComp1, physComp2;
    private PositionComp posComp1, posComp2;

    private Vec2 collisionVector;
    private float penetrationDepth;


    public CollisionData(int e1, CollisionComp collComp1, PositionComp posComp1, PhysicsComp physicsComp1, int e2, CollisionComp collComp2, PositionComp posComp2, PhysicsComp physicsComp2){
        this.entity1 = e1;
        this.entity2 = e2;

        this.collComp1 = collComp1;
        this.collComp2 = collComp2;

        this.posComp1 = posComp1;
        this.posComp2 = posComp2;

        this.physComp1 = physicsComp1;
        this.physComp2 = physicsComp2;

    }

    public int getEntity1() {
        return entity1;
    }
    public int getEntity2() {
        return entity2;
    }

    public CollisionComp getCollComp1() {
        return collComp1;
    }
    public CollisionComp getCollComp2() {
        return collComp2;
    }

    public PhysicsComp getPhysicsComp1() {

        return physComp1;
    }
    public PhysicsComp getPhysicsComp2() {
        return physComp2;
    }

    public PositionComp getPosComp1() {
        return posComp1;
    }
    public PositionComp getPosComp2() {
        return posComp2;
    }

    public void swapEntities() {
        int nentity1 = this.entity1;
        CollisionComp ncollComp1 = this.collComp1;
        PositionComp nposComp1 = this.posComp1;
        PhysicsComp nphysComp1 = this.physComp1;

        entity1 = entity2;
        collComp1 = collComp2;
        posComp1 = posComp2;
        physComp1 = physComp2;

        entity2 = nentity1;
        collComp2 = ncollComp1;
        posComp2  = nposComp1;
        physComp2 = nphysComp1;
    }


    public void setCollisionVector(Vec2 vector){this.collisionVector = vector; }

    public void setPenetrationDepth(float penetrationDepth) {this.penetrationDepth = penetrationDepth; }

    public Vec2 getCollisionVector() {return collisionVector; }

    public float getPenetrationDepth() {return penetrationDepth;
    }




}
