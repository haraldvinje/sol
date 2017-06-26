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
        Set<Integer> abilityCompEntities = worldContainer.getEntitiesWithComponentType(AbilityComp.class);

        for (int entity: abilityCompEntities){
            AbilityComp abComp = (AbilityComp) worldContainer.getComponent(entity, AbilityComp.class);


            PositionComp posComp = (PositionComp) worldContainer.getComponent(entity, PositionComp.class);
            RotationComp rotComp = (RotationComp) worldContainer.getComponent(entity, RotationComp.class);

            for (MeleeAbility meleeAbility: abComp.getMeleeAbilities()) {
                if (abComp.getOccupiedBy() == meleeAbility || abComp.getOccupiedBy() == null){
                    updateMeleeAbility(meleeAbility, abComp, posComp, rotComp);
                }
            }
        }
    }

    private void updateMeleeAbility(MeleeAbility meleeAbility, AbilityComp abComp, PositionComp playerPosComp, RotationComp playerRotComp){

        int hitboxEntity = meleeAbility.getHitboxEntity();

       /* PhysicsComp meleePhComp = (PhysicsComp)(worldContainer.getComponent(hitboxEntity, PhysicsComp.class));
        PositionComp meleePosComp = (PositionComp) (worldContainer.getComponent(hitboxEntity, PositionComp.class));*/

        int startupTime = meleeAbility.getStartupTime();
        int activeHitboxTime = meleeAbility.getActiveHitboxTime();
        int endingLagTime = meleeAbility.getEndlagTime();
        int attackDurationTime = startupTime + activeHitboxTime + endingLagTime;
        int rechargeTime = meleeAbility.getRechargeTime();

/*
        meleePhComp.resetVelocity();
*/


        if (meleeAbility.isRequestingExecution()){
            meleeAbility.execute();
            meleeAbility.setRequestExecution(false);
        }

        if (meleeAbility.isExecuting()) {
            //if not occupied
            if (meleeAbility.counter==0){
                abComp.setOccupiedBy(meleeAbility);
            }

            meleeAbility.counter++;

            if (meleeAbility.counter == startupTime) {
                activateHitbox(meleeAbility);
            }

            else if (startupTime + activeHitboxTime > meleeAbility.counter){
                duringActiveHitbox(meleeAbility, playerPosComp, playerRotComp);

            }

            else if (startupTime + activeHitboxTime == meleeAbility.counter){
                duringEndingLag(meleeAbility);
            }

            else if (attackDurationTime == meleeAbility.counter){
                duringRecharge(abComp);
            }

            else if (attackDurationTime+rechargeTime == meleeAbility.counter) {
                afterRecharge(meleeAbility);
            }
        }
    }



    private void activateHitbox(MeleeAbility meleeAbility){
        meleeAbility.setActiveHitbox(true);
    }


    private void duringActiveHitbox(MeleeAbility meleeAbility, PositionComp playerPosComp, RotationComp playerRotCom ){
        float positionAngle = playerRotCom.getAngle();
        float relativeHitboxAngle = meleeAbility.getRelativeAngle();
        float resultAngle = positionAngle + relativeHitboxAngle;

        Vec2 vector = Vec2.newLenDir(meleeAbility.getRelativeDistance(),resultAngle);
        Vec2 result = playerPosComp.getPos().add(vector);
        meleeAbility.setPosition(result);

        meleeAbility.setAngle(resultAngle);

    }

    private void duringEndingLag(MeleeAbility meleeAbility){
        meleeAbility.setActiveHitbox(false);
    }

    private void duringRecharge(AbilityComp abComp) {
        abComp.setOccupiedBy(null);
    }

    private void afterRecharge(MeleeAbility meleeAbility){
        meleeAbility.setExecuting(false);
    }



}
