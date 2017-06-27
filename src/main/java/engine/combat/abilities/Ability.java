package engine.combat.abilities;

import engine.WorldContainer;
import engine.physics.Circle;
import engine.physics.Rectangle;
import engine.physics.Shape;
import game.GameUtils;

/**
 * Created by eirik on 19.06.2017.
 */
public abstract class Ability {

    private int abilityId;

    private int startupTime;
    private int activeHitboxTime;
    private int endlagTime;
    private int rechargeTime;

    private boolean recharging = false;

    private boolean requestExecution;

    public int counter;



    public Ability(WorldContainer wc, float damage, float knockbackRatio, int startupTime, int activeHitboxTime, int endlagTime, int rechargeTime){
        this.startupTime = startupTime;
        this.activeHitboxTime = activeHitboxTime;
        this.endlagTime = endlagTime;
        this.rechargeTime = rechargeTime;
    }
//    public MeleeAbility(WorldContainer wc){
//        this(wc, 5, 0.5f, new Circle(5), 0.0f, 0.0f, 10, 10, 10, 10);
//    }

    abstract void startEffect();
    abstract void duringEffect();
    abstract void endEffect();

    //to be called by abilityCom
    void setAbilityId(int id) {
        this.abilityId = id;
    }
    public int getAbilityId() {
        return abilityId;
    }

    public void requestExecution() {
        requestExecution = true;

    }

    boolean isRequestingExecution() {
        return requestExecution;
    }
    void setRequestExecution(boolean b){
        this.requestExecution = b;
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


}
