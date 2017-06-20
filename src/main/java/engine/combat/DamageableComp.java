package engine.combat;

import engine.Component;

/**
 * Created by eirik on 19.06.2017.
 */
public class DamageableComp implements Component {

    private float damaged = 0;


    public float getDamage() {
        return damaged;
    }
    public void applyDamage(float dmg) {
        damaged += dmg;
    }
}
