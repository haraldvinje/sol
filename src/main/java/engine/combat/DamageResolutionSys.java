package engine.combat;

import engine.RotationComp;
import engine.Sys;
import engine.WorldContainer;
import engine.combat.abilities.AbilityComp;
import engine.combat.abilities.ProjectileComp;
import engine.physics.CollisionComp;
import engine.physics.CollisionCompIterator;
import engine.physics.CollisionData;
import engine.physics.PhysicsComp;
import utils.maths.M;
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

        //reset deltDamage flags in damager
        for (int entity : wc.getEntitiesWithComponentType(DamagerComp.class)) {
            DamagerComp dmgerComp = (DamagerComp) wc.getComponent(entity, DamagerComp.class);

            dmgerComp.resetDeltDamage();
        }

        for (int entity : wc.getEntitiesWithComponentType(DamageableComp.class)) {
            //reset flags
            DamageableComp dmgableComp = (DamageableComp) wc.getComponent(entity, DamageableComp.class);
            dmgableComp.resetInterrupt();

            //update damageable with respect to damagers
            CollisionComp collComp = (CollisionComp) wc.getComponent(entity, CollisionComp.class);
            updateDamageableEntity(entity, collComp);
        }

    }

    @Override
    public void terminate() {

    }

    private void updateDamageableEntity(int entity, CollisionComp collComp) {

        //decrement stun timer if stunned
        updateStunTimer(entity);

        //check for each colliding object if it is a damager
        CollisionCompIterator collIt = collComp.collisionCompIterator();

        while(collIt.hasNext()) {
            CollisionData data = collIt.next();

            if (!data.isActive()) continue;


            int damagerEntity = collIt.getOtherEntity();

            if (wc.hasComponent(damagerEntity, DamagerComp.class)) {
                //System.out.println("Taking damage, bullet: "+collIt.getOtherEntity() +" victim: " + collIt.getSelfEntity());
                takeDamage(entity, collIt.getOtherEntity());
            }

        }
    }

    private void updateStunTimer(int dmgableEntity) {
        DamageableComp dmgablComp = (DamageableComp)wc.getComponent(dmgableEntity, DamageableComp.class);

        if (dmgablComp.isStunned()) {
            dmgablComp.decrementStunTimer();
        }
    }

    private void takeDamage(int damaged, int damager) {
        DamagerComp dmgerComp = (DamagerComp)wc.getComponent(damager, DamagerComp.class);
        //PhysicsComp dmgerPhysComp = (PhysicsComp)wc.getComponent(damager, PhysicsComp.class);
        RotationComp dmgerRotComp = (RotationComp)wc.getComponent(damager, RotationComp.class);

        DamageableComp dmgablComp = (DamageableComp)wc.getComponent(damaged, DamageableComp.class);
        PhysicsComp dmgablPhysComp = (PhysicsComp)wc.getComponent(damaged, PhysicsComp.class);


        //reset physics for damageable. Should happen before naturalResolution
        dmgablPhysComp.reset();

        //calculate damage and knockback
        float damage = dmgerComp.getDamage();
        float knockbackLen = M.pow(dmgablComp.getDamage() * dmgerComp.getKnockbackRatio(), 1 ) + dmgerComp.getBaseKnockback();
        float knockbackDir = dmgerRotComp.getAngle();

        //frames stunned
        int stunDuration = (int)knockbackLen/60;

        //apply damage and knockback
        dmgablComp.applyDamage(damage);
        dmgablPhysComp.addImpulse( Vec2.newLenDir(knockbackLen, knockbackDir) );

        //apply hitstun
        dmgablComp.setStunTimer(stunDuration);


        //set deltDamage and interrupt flags
        dmgerComp.deltDamage();
        dmgablComp.interrupt();
    }
}
