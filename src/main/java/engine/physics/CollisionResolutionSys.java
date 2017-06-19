package engine.physics;

import engine.PositionComp;
import engine.Sys;
import engine.WorldContainer;

import utils.maths.M;
import utils.maths.Vec2;

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

            for (CollisionData data: cc1.getCollisionDataList()){
                //calculate vector based on current velocity vector,  penetration depth
                resolveCollision(data);
            }

            cc1.reset();
        }
        //need to get all collisionComponents
        //run over every collisionDataList for every collisionComponent
        //add CorrectVector to velocitycomponent of based on ID
        //success
    }

    private void resolveCollision(CollisionData data) {
        PhysicsComp phc1 = (PhysicsComp) worldContainer.getComponent(data.getEntity1(), PhysicsComp.class);
        PhysicsComp phc2 = (PhysicsComp) worldContainer.getComponent(data.getEntity2(), PhysicsComp.class);
        Vec2 collisionVector = data.getCollisionVector();

        Vec2 relVelocity = phc2.getVelocity().subtract(phc1.getVelocity());

        float velAlongNormal = relVelocity.dotProduct(collisionVector);

        if (velAlongNormal > 0) { //do not resolve collision if objects are moving apart
            return;
        }


        float invMass1 = phc1.getInvMass();
        float invMass2 = phc2.getInvMass();

        float elasticity = M.min(phc1.getElasticity(), phc2.getElasticity());

        float impulseLength = -(1 + elasticity) * velAlongNormal;
        impulseLength /= invMass1 + invMass2;

        //apply impulse
        Vec2 impulseVec = collisionVector.scale(impulseLength); //-(1 + elasticity)*velAlongNormal;

        phc1.addImpulse(impulseVec.scale(invMass1).negative() );
        phc2.addImpulse(impulseVec.scale(invMass2) );

        positionalCorrection(data);
    }


    private void positionalCorrection(CollisionData data)
    {
        PhysicsComp physComp1 = data.getPhysicsComp1();
        PhysicsComp physComp2 = data.getPhysicsComp2();

        PositionComp posComp1 = data.getPosComp1();
        PositionComp posComp2 = data.getPosComp2();

        float percent = 0.2f; // usually 20% to 80%
        float slop = 0.01f; // usually 0.01 to 0.1

        Vec2 correction = data.getCollisionVector().scale(percent * (M.max(data.getPenetrationDepth() - slop, 0.0f) / (physComp1.getInvMass() + physComp2.getInvMass()) ) );
        posComp1.addPos( correction.scale(physComp1.getInvMass()).negative() );
        posComp2.addPos( correction.scale(physComp2.getInvMass()) );
//		p1.addTemporaryVelocity( correction.scale(p1.getInvMass()).negative() );
//		p2.addTemporaryVelocity( correction.scale(p2.getInvMass()) );
    }

}
