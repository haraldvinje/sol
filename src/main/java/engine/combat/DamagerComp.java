package engine.combat;

import engine.Component;

/**
 * Created by eirik on 20.06.2017.
 */
public class DamagerComp implements Component {


    private float damage;
    private float knockbackRatio;


    public DamagerComp(float damage, float knockbackRatio) {
        setDamage(damage);
        setKnockbackRatio(knockbackRatio);
    }


    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getKnockbackRatio() {
        return knockbackRatio;
    }

    public void setKnockbackRatio(float knockbackRatio) {
        this.knockbackRatio = knockbackRatio;
    }
}
