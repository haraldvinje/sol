package engine.combat;

import engine.Sys;
import engine.WorldContainer;
import engine.physics.CollisionComp;
import engine.physics.CollisionCompIterator;
import engine.physics.CollisionData;
import engine.physics.PhysicsComp;
import utils.maths.Vec2;

/**
 * Created by eirik on 19.06.2017.
 */
public class DamageResolutionSys implements Sys {


    private WorldContainer wc;



    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.wc = wc;
    }

    @Override
    public void update() {

        for (int entity : wc.getEntitiesWithComponentType(DamageableComp.class)) {

            CollisionComp collComp = (CollisionComp) wc.getComponent(entity, CollisionComp.class);

            updateDamageableEntity(entity, collComp);
        }

    }

    private void updateDamageableEntity(int entity, CollisionComp collComp) {
        //check for each colliding object if it is a damager
        CollisionCompIterator collIt = collComp.collisionCompIterator();
        while(collIt.hasNext()) {
            CollisionData data = collIt.next();

            //System.out.println(data);

            if (wc.hasComponent(collIt.getOtherEntity(), DamagerComp.class)) {
                System.out.println("Taking damage, bullet: "+collIt.getOtherEntity() +" victim: " + collIt.getSelfEntity());
                takeDamage(entity, collIt.getOtherEntity());
            }
        }
    }

    private void takeDamage(int damaged, int damager) {
        DamagerComp dmgerComp = (DamagerComp)wc.getComponent(damager, DamagerComp.class);
        PhysicsComp dmgerPhysComp = (PhysicsComp)wc.getComponent(damager, PhysicsComp.class);

        DamageableComp dmgablComp = (DamageableComp)wc.getComponent(damaged, DamageableComp.class);
        PhysicsComp dmgablPhysComp = (PhysicsComp)wc.getComponent(damaged, PhysicsComp.class);

        float damage = dmgerComp.getDamage();
        float knockbackLen = dmgablComp.getDamage() * dmgerComp.getKnockbackRatio();
        float knockbackDir = dmgerPhysComp.getVelocity().getDirection();

        dmgablComp.applyDamage(damage);
        dmgablPhysComp.addAcceleration( Vec2.newLenDir(knockbackLen, knockbackDir) );

        System.out.println("Sandbag damage:"+dmgablComp.getDamage());
        System.out.println("Sandbag hit with knockback:"+knockbackLen);
    }
}
