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


    private int abilityId;

    private int hitboxEntity;

    private float relativeDistance;
    private float relativeAngle;

    private int startupTime;
    private int activeHitboxTime;
    private int endlagTime;
    private int rechargeTime;

    private boolean recharging = false;

    private boolean requestExecution;
//    private boolean activeHitbox = false;

//    private boolean executing = false;
    public int counter;



    public MeleeAbility(WorldContainer wc, float damage, float knockbackRatio, Shape hitboxShape, float relativeDistance, float relativeAngle, int startupTime, int activeHitboxTime, int endlagTime, int rechargeTime){
        this.startupTime = startupTime;
        this.activeHitboxTime = activeHitboxTime;
        this.endlagTime = endlagTime;
        this.rechargeTime = rechargeTime;

        this.relativeDistance = relativeDistance;
        this.relativeAngle = relativeAngle;

        if (hitboxShape instanceof Circle){
            hitboxEntity = GameUtils.allocateHitboxEntity(wc, (Circle)hitboxShape, damage, knockbackRatio);
        }

        if (hitboxShape instanceof Rectangle){
            throw new UnsupportedOperationException("Cannot have rectangle hitboxes as of now");
        }
    }
    public MeleeAbility(WorldContainer wc){
        this(wc, 5, 0.5f, new Circle(5), 0.0f, 0.0f, 10, 10, 10, 10);
    }

    //to be called by abilityComp
    @Override
    void setAbilityId(int id) {
        this.abilityId = id;
    }
    @Override
    public int getAbilityId() {
        return abilityId;
    }

    public void requestExecution() {
        requestExecution = true;

    }


    float getRelativeDistance() {
        return relativeDistance;
    }
    float getRelativeAngle() {return relativeAngle;}

    boolean isRequestingExecution() {
        return requestExecution;
    }
    void setRequestExecution(boolean b){
        this.requestExecution = b;
    }

    @Override
    int getHitboxEntity() {
        return hitboxEntity;
    }

    int getStartupTime() {
        return startupTime;
    }
    int getActiveHitboxTime() {
        return activeHitboxTime;
    }
    int getEndlagTime() {
        return endlagTime;
    }
    int getRechargeTime() {
        return rechargeTime;
    }

    boolean isRecharging() {
        return recharging;
    }
    void setRecharging(boolean recharge) {
        this.recharging = recharge;
    }


//    public void setExecuting(boolean status){
//        this.executing = status;
//    }
//    public boolean isExecuting(){
//        return this.executing;
//    }


//    public void setActiveHitbox(boolean active) {
//        if (!this.activeHitbox && active){
//            activateComponents();
//        }
//        else if (this.activeHitbox && !active){
//            deactivateComponents();
//        }
//        this.activeHitbox = active;
//    }


//    private void deactivateComponents(){
//        worldContainer.deactivateEntity(hitboxEntity);
//    }
//
//    private void activateComponents(){
//        worldContainer.activateEntity(hitboxEntity);
//        PhysicsComp phComp = (PhysicsComp) worldContainer.getComponent(hitboxEntity, PhysicsComp.class);
//        phComp.resetVelocity();
//    }



//    public boolean isActiveHitbox() {
//        return activeHitbox;
//    }



//    public Shape getHitbox() {
//        return hitbox;
//    }

//    public void setHitbox(Shape hitbox) {
//        this.hitbox = hitbox;
//    }



}
