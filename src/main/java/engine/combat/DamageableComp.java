package engine.combat;

import engine.Component;

/**
 * Created by eirik on 19.06.2017.
 */
public class DamageableComp implements Component {

    private float damaged = 0;

    private int stunTimer = 0;

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
    void resetInterrupt() {
        interrupted = false;
    }

    public boolean isStunned() {
        return stunTimer > 0;
    }
    void decrementStunTimer() {
        stunTimer--;
    }
    int getStunTimer() {
        return stunTimer;
    }
    void setStunTimer(int time) {
        stunTimer = time;
    }

    public void reset() {
        damaged = 0;
        stunTimer = 0;
    }
}
