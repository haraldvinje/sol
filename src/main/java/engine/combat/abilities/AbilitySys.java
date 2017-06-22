package engine.combat.abilities;

import engine.PositionComp;
import engine.RotationComp;
import engine.Sys;
import engine.WorldContainer;
import engine.physics.PhysicsComp;
import javafx.geometry.Pos;
import utils.maths.Vec2;

import java.util.Set;

/**
 * Created by haraldvinje on 21-Jun-17.
 */
public class AbilitySys implements Sys {

    WorldContainer worldContainer;

    @Override

    public void setWorldContainer(WorldContainer wc) {
        this.worldContainer = wc;
    }

    @Override
    public void update() {
        Set<Integer> abilityComps = worldContainer.getEntitiesWithComponentType(AbilityComp.class);
        for (int entity: abilityComps){

            AbilityComp abComp = (AbilityComp) worldContainer.getComponent(entity, AbilityComp.class);

            if (abComp.isOccupied()){
                continue;
            }

            PositionComp posComp = (PositionComp) worldContainer.getComponent(entity, PositionComp.class);
            RotationComp rotComp = (RotationComp) worldContainer.getComponent(entity, RotationComp.class);

            float x = posComp.getX();
            float y = posComp.getY();
            float angle = rotComp.getAngle();

            for (MeleeAbility meleeAbility: abComp.getMeleeAbilities()){
                int hitboxEntity = meleeAbility.getHitboxEntity();
                PhysicsComp phComp = (PhysicsComp)(worldContainer.getComponent(hitboxEntity, PhysicsComp.class));
                PositionComp mPosComp = (PositionComp) (worldContainer.getComponent(hitboxEntity, PositionComp.class));

                int startupTime = meleeAbility.getStartupTime();
                int activeHitboxTime = meleeAbility.getActiveHitboxTime();
                int endingLagTime = meleeAbility.getEndlagTime();
                int attackDurationTime = startupTime + activeHitboxTime + endingLagTime;
                int rechargeTime = meleeAbility.getRechargeTime();

                phComp.setVelocity(new Vec2(0,0));
                if (meleeAbility.isExecuting()) {
                    abComp.setOccupied(true);
                    meleeAbility.counter++;
                    if (startupTime + activeHitboxTime>meleeAbility.counter && meleeAbility.counter>=startupTime){
                        meleeAbility.setActiveHitbox(true);
                        Vec2 vector = Vec2.newLenDir(meleeAbility.getRelativeDistance(), angle);
                        Vec2 result = new Vec2(x, y).add(vector);
                        mPosComp.setPos(result);
                        System.out.println("During hitbox");

                    }
                    else if (attackDurationTime>meleeAbility.counter && meleeAbility.counter>=startupTime + activeHitboxTime){
                        meleeAbility.setActiveHitbox(false);
                        System.out.println("During ending lag");
                    }
                    else if (attackDurationTime + rechargeTime>meleeAbility.counter && meleeAbility.counter>=attackDurationTime){
                        abComp.setOccupied(false);
                        System.out.println("During recharge phase");
                    }
                    else if (meleeAbility.counter>=attackDurationTime+rechargeTime) {
                        meleeAbility.setExecuting(false);
                        System.out.println("Now you can execute attack again :)");
                    }
                    else{
                        System.out.println("In startup frames");
                    }
                }
            }

        }
    }
}
