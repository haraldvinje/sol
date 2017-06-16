package engine.physics;

import com.sun.javafx.collections.VetoableListDecorator;
import engine.Component;
import engine.PositionComp;
import engine.Sys;
import engine.WorldContainer;
import utils.maths.Vec2;
import javafx.geometry.Pos;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by haraldvinje on 15-Jun-17.
 */
public class PhysicsSys implements Sys {

    private WorldContainer worldContainer;
    private Set<Integer> physicsEntities;

    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.worldContainer = wc;
    }

    @Override
    public void update() {
        this.physicsEntities = worldContainer.getEntitiesWithComponentType(PhysicsComp.class);
        applyFriction();            //adding friction acceleration vector
        updateVelocities();         //accelerating
        updatePositions();
        resetAcceleration();
    }

    private void applyFriction() {
        for (int entity: physicsEntities){
            PhysicsComp physicsComp = (PhysicsComp) worldContainer.getComponent(entity, PhysicsComp.class);
            Vec2 frictionVector = ((PhysicsComp) worldContainer.getComponent(entity, PhysicsComp.class)).getVelocity().negative();
            float frictionConst = physicsComp.getFrictionConst();
            frictionVector = frictionVector.scale(physicsComp.getVelocity().getLength()*frictionConst);

            //physicsComp.addVelocity(frictionVector);
            physicsComp.addAcceleration(physicsComp.getVelocity().scale(0.1f).negative());
        }
    }

    private void updateVelocities(){
        for (int entity: physicsEntities){
            PhysicsComp physicsComp = (PhysicsComp) worldContainer.getComponent(entity, PhysicsComp.class);
            Vec2 acceleration = ((PhysicsComp) worldContainer.getComponent(entity, PhysicsComp.class)).getAcceleration();
            physicsComp.addVelocity(acceleration);


        }
    }



    private void updatePositions(){
        for (int entity: physicsEntities){
            PositionComp posComp = (PositionComp) worldContainer.getComponent(entity, PositionComp.class);
            Vec2 velocity = ((PhysicsComp) worldContainer.getComponent(entity, PhysicsComp.class)).getVelocity();
            posComp.addPos(velocity);

        }
    }

    private void resetAcceleration() {
        for (int entity: physicsEntities){
            PhysicsComp physicsComp = (PhysicsComp) worldContainer.getComponent(entity, PhysicsComp.class);
            physicsComp.resetAcceleration();
        }
    }
}
