package engine.combat.abilities;

import engine.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by eirik on 19.06.2017.
 */
public class AbilityComp implements Component {

    private static final int EXPECTED_MELEE_ABILITIES = 2;
    private static final int EXPECTED_PROJECTILE_ABILITIES = 2;

    private List<MeleeAbility> meleeAbilities = new ArrayList<>(EXPECTED_MELEE_ABILITIES);
    private List<ProjectileAbility> projectileAbilities = new ArrayList<>(EXPECTED_PROJECTILE_ABILITIES);

    private Ability occupiedBy;

    public void addMeleeAbility(MeleeAbility meleeAbility){
        meleeAbilities.add(meleeAbility);
    }

    public List<MeleeAbility> getMeleeAbilities(){
        return meleeAbilities;
    }

    public Ability getOccupiedBy(){
        return occupiedBy;
    }


    public void requestExecution(int meleeId) {
        meleeAbilities.get(meleeId).requestExecution();
    }

    public void setOccupiedBy(Ability a) {
        this.occupiedBy = a;
    }
}
