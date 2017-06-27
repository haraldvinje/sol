package engine.combat;

import engine.Component;

/**
 * Created by eirik on 19.06.2017.
 */
public class DamageableComp implements Component {

    private float damaged = 0;

    private boolean interrupted = false;


    public float getDamage() {
        return damaged;
    }
    public void applyDamage(float dmg) {
        damaged += dmg;
    }

    /**
     * Set an interrupt flag that can be used to abort abilities
     */
    public void interrupt() {
        interrupted = true;
    }
    public boolean isInterrupted() {
        return interrupted;
    }
    public void popInterrupt() {
        interrupted = false;
    }

    public void reset() {
        damaged = 0;
    }
}
