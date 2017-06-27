package engine.combat.abilities;

import engine.PositionComp;
import engine.RotationComp;
import engine.WorldContainer;
import engine.physics.Circle;
import engine.physics.PhysicsComp;
import engine.physics.Shape;
import game.GameUtils;
import utils.maths.Vec2;

/**
 * Created by eirik on 19.06.2017.
 */
public class ProjectileAbility extends Ability {


    private final float startDistance = 64f;

    private int projEntity;

    private float projStartSpeed;
    private float knockbackAngle;



    public ProjectileAbility(WorldContainer wc, int startupTime, int endlagTime, int rechargeTime,     float damage, float knockbackRatio, float projStartSpeed, float projKnockbackAngle, Shape projShape) {
        super(wc, startupTime, 0, endlagTime, rechargeTime);

        projEntity = GameUtils.allocateProjectileEntity(wc, (Circle)projShape, damage, knockbackRatio);

        this.projStartSpeed = projStartSpeed;
        this.knockbackAngle = projKnockbackAngle;
    }

    @Override
    void startEffect(WorldContainer wc, int requestingEntity) {
        wc.activateEntity(projEntity);

        PositionComp reqPosComp = (PositionComp)wc.getComponent(requestingEntity, PositionComp.class);
        RotationComp reqRotComp = (RotationComp)wc.getComponent(requestingEntity, RotationComp.class);

        PositionComp projPosComp = (PositionComp)wc.getComponent(projEntity, PositionComp.class);
        RotationComp projRotComp = (RotationComp)wc.getComponent(projEntity, RotationComp.class);
        PhysicsComp projPhysComp = (PhysicsComp)wc.getComponent(projEntity, PhysicsComp.class);
        HitboxComp projHitbComp = (HitboxComp) wc.getComponent(projEntity, HitboxComp.class);


        float velDir = reqRotComp.getAngle();

        //set hitbox comp state
        projHitbComp.reset();
        projHitbComp.setOwner(requestingEntity);

        //set initial position
        Vec2 relPos = Vec2.newLenDir(startDistance, velDir );
        projPosComp.setPos( reqPosComp.getPos().add(relPos) );

        //set initial velocity

        Vec2 startVel = Vec2.newLenDir(projStartSpeed, velDir);
        projPhysComp.reset();
        projPhysComp.addImpulse(startVel);

        //set knockback direction
        projRotComp.setAngle(velDir + knockbackAngle);
    }

    @Override
    void duringEffect(WorldContainer wc, int requestingEntity) {

    }

    @Override
    void endEffect(WorldContainer wc, int requestingEntity) {

    }
}
