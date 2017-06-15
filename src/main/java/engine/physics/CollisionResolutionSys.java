package engine.physics;

import engine.Sys;
import engine.WorldContainer;

import engine.maths.Vec2;

import java.util.Set;

/**
 * Created by haraldvinje on 14-Jun-17.
 */
public class CollisionResolutionSys implements Sys {

    private WorldContainer worldContainer;

    public CollisionResolutionSys(){

    }


    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.worldContainer = wc;
    }

    @Override
    public void update() {
        Set<Integer> collisionEntitiesSet = worldContainer.getEntitiesWithComponentType(CollisionComp.class);
        //TODO: assuming that all entities with collisioncomp also have velocitycomp. Write test for this later
        for (int entity: collisionEntitiesSet){
            CollisionComp cc1 = (CollisionComp) worldContainer.getComponent(entity, CollisionComp.class);

            for (CollisionData cd: cc1.getCollisionDataList()){
                //calculate vector based on current velocity vector,  penetration depth
                resolveCollision(cd);
            }
        }
        //need to get all collisionComponents
        //run over every collisionDataList for every collisionComponent
        //add CorrectVector to velocitycomponent of based on ID
        //success
    }

    private void resolveCollision(CollisionData cd){
        PhysicsComp phc1 = (PhysicsComp) worldContainer.getComponent(cd.getEntity1(), PhysicsComp.class);
        PhysicsComp phc2 = (PhysicsComp) worldContainer.getComponent(cd.getEntity2(), PhysicsComp.class);

        Vec2 relVelocity = phc2.getVelocity().subtract(phc1.getVelocity());
        float velAlongNormal = relVelocity.dotProduct(cd.getCollisionVector());

        if (velAlongNormal > 0) { //do not resolve collision if objects are moving apart
            return;
        }

        float elasticity = 1;
        float mass1 = 1;
        float mass2 = 1;
        float impulse = -(1 + elasticity)*velAlongNormal;
        impulse /= mass1 + mass2;
        Vec2 norm = cd.getCollisionVector();
        norm.scale(impulse);

        phc1.addVelocity(norm);
        phc2.addVelocity(norm.negative());


    }







}
