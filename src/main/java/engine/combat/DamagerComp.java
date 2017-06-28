package engine.combat;

import engine.Component;

/**
 * Created by eirik on 20.06.2017.
 */
public class DamagerComp implements Component {


    private float damage;
    private float baseKnockback;
    private float knockbackRatio;


    private boolean deltDamageFlag = false; //is reset by damageResolutionSys every frame


    public DamagerComp(float damage, float baseKnockback, float knockbackRatio) {
        setDamage(damage);
        setBaseKnockback(baseKnockback);
        setKnockbackRatio(knockbackRatio);
    }


    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getBaseKnockback() {
        return baseKnockback;
    }
    public void setBaseKnockback(float baseKnockback) {
        this.baseKnockback = baseKnockback;
    }

    public float getKnockbackRatio() {
        return knockbackRatio;
    }

    public void setKnockbackRatio(float knockbackRatio) {
        this.knockbackRatio = knockbackRatio;
    }

    public boolean hasDeltDamage() {
        return deltDamageFlag;
    }
    public void deltDamage() {
        deltDamageFlag = true;
    }
    void resetDeltDamage() {
        deltDamageFlag = false;
    }
}
