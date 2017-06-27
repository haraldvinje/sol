package engine.combat.abilities;

import engine.Component;
import engine.PositionComp;
import engine.RotationComp;
import engine.WorldContainer;
import engine.character.UserCharacterInputComp;
import engine.combat.DamagerComp;
import engine.graphics.ColoredMesh;
import engine.graphics.ColoredMeshComp;
import engine.graphics.ColoredMeshUtils;
import engine.physics.*;
import game.GameUtils;
import org.w3c.dom.css.Rect;
import utils.maths.Vec2;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

/**
 * Created by eirik on 19.06.2017.
 */
public class MeleeAbility extends Ability{


    private int hitboxEntity;

    private float relativeDistance;
    private float relativeAngle;


    public MeleeAbility(WorldContainer wc, int startupTime, int activeHitboxTime, int endlagTime, int rechargeTime,     float damage, float baseKnockback,  float knockbackRatio, Shape hitboxShape, float relativeDistance, float relativeAngle){
        super(wc, startupTime, activeHitboxTime, endlagTime, rechargeTime);

        this.relativeDistance = relativeDistance;
        this.relativeAngle = relativeAngle;

        if (hitboxShape instanceof Circle){
            hitboxEntity = GameUtils.allocateHitboxEntity(wc, (Circle)hitboxShape, damage, baseKnockback, knockbackRatio);
        }

        if (hitboxShape instanceof Rectangle){
            throw new UnsupportedOperationException("Cannot have rectangle hitboxes as of now");
        }
    }
//    public MeleeAbility(WorldContainer wc){
//        this(wc, 5, 0.5f, new Circle(5), 0.0f, 0.0f, 10, 10, 10, 10);
//    }


    float getRelativeDistance() {
        return relativeDistance;
    }
    float getRelativeAngle() {return relativeAngle;}

    int getHitboxEntity() {
        return hitboxEntity;
    }

    @Override
    public void startEffect(WorldContainer wc, int requestingEntity) {
        wc.activateEntity(hitboxEntity);

        PositionComp reqPosComp = (PositionComp)wc.getComponent(requestingEntity, PositionComp.class);
        RotationComp reqRotComp = (RotationComp)wc.getComponent(requestingEntity, RotationComp.class);

        RotationComp hbRotComp = (RotationComp)wc.getComponent(hitboxEntity, RotationComp.class);
        HitboxComp hbHitbComp = (HitboxComp) wc.getComponent(hitboxEntity, HitboxComp.class);

        //set hitbox comp state
        hbHitbComp.reset();
        hbHitbComp.setOwner(requestingEntity);

        //set hitbox knockback direction
        float hitboxAngle = reqRotComp.getAngle() + relativeAngle;
        hbRotComp.setAngle(hitboxAngle);

        //set relative positiom
        positionHitbox(wc, requestingEntity);
    }

    @Override
    public void duringEffect(WorldContainer wc, int requestingEntity) {
        positionHitbox(wc, requestingEntity);
    }

    @Override
    public void endEffect(WorldContainer wc, int requestingEntity) {
        //deactivate hitbox
        wc.deactivateEntity(hitboxEntity);
    }

    private void positionHitbox(WorldContainer wc, int requestingEntity) {
        PositionComp reqPosComp = (PositionComp)wc.getComponent(requestingEntity, PositionComp.class);
        RotationComp reqRotComp = (RotationComp)wc.getComponent(requestingEntity, RotationComp.class);

        PositionComp hbPosComp = (PositionComp)wc.getComponent(hitboxEntity, PositionComp.class);

        Vec2 relPos = Vec2.newLenDir(relativeDistance, reqRotComp.getAngle() + relativeAngle );
        hbPosComp.setPos( reqPosComp.getPos().add(relPos) );

        //reset physics
        ((PhysicsComp)wc.getComponent(hitboxEntity, PhysicsComp.class) ).reset();
    }

}
