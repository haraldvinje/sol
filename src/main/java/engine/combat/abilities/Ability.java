package engine.combat.abilities;

/**
 * Created by eirik on 19.06.2017.
 */
public abstract class Ability {


    abstract void setAbilityId(int id);
    public abstract int getAbilityId();

    abstract int getHitboxEntity();
}
