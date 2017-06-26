package engine.combat.abilities;

import engine.PositionComp;
import engine.RotationComp;
import engine.Sys;
import engine.WorldContainer;
import engine.physics.PhysicsComp;
import javafx.geometry.Pos;
import utils.maths.Vec2;

import java.awt.geom.RoundRectangle2D;
import java.util.Set;

/**
 * Created by haraldvinje on 21-Jun-17.
 */
public class AbilitySys implements Sys {

    WorldContainer wc;

    @Override

    public void setWorldContainer(WorldContainer wc) {
        this.wc = wc;
    }

    @Override
    public void update() {
        Set<Integer> abilityCompEntities = wc.getEntitiesWithComponentType(AbilityComp.class);

        for (int entity: abilityCompEntities){
            PositionComp posComp = (PositionComp) wc.getComponent(entity, PositionComp.class);
            RotationComp rotComp = (RotationComp) wc.getComponent(entity, RotationComp.class);
            AbilityComp abComp = (AbilityComp) wc.getComponent(entity, AbilityComp.class);

            if (abComp.isAbortExecution()) {
                abComp.resetAbortExecution();
                abortAbilityExecution(abComp);
            }

            for (MeleeAbility meleeAbility: abComp.getMeleeAbilities()) {
                updateMeleeAbility(meleeAbility, abComp, posComp, rotComp);
            }
        }
    }

    @Override
    public void terminate() {

    }

    private void abortAbilityExecution(AbilityComp abComp) {
        if (abComp.getOccupiedBy() != null) {
            MeleeAbility ab = (MeleeAbility)abComp.getOccupiedBy();

            //deactivate hitbox
            //wc.deactivateEntity(ab.getHitboxEntity());
            ab.setRecharging(false);

            abComp.setOccupiedBy(null);
        }
    }

    private void updateMeleeAbility(MeleeAbility meleeAbility, AbilityComp abComp, PositionComp posComp, RotationComp rotComp){

        int startupTime = meleeAbility.getStartupTime();
        int activeHitboxTime = meleeAbility.getActiveHitboxTime();
        int endingLagTime = meleeAbility.getEndlagTime();
        int rechargeTime = meleeAbility.getRechargeTime();


        //move to next frame. Even thoug it is not executing
        meleeAbility.counter++;

        //if this ability is recharging, continue recharging and do nothing else
        if (! meleeAbility.isRecharging()) {

            //if no ability is executing, check if this one should be executed
            if (abComp.getOccupiedBy() == null) {
                //is ability is requested, execute it
                if (meleeAbility.isRequestingExecution()) {

                    System.out.println("Activating ability");

                    startAbility(abComp, meleeAbility);
                }
            }

            //if this ability should execute, do it
            if (abComp.getOccupiedBy() == meleeAbility) {

                if (meleeAbility.counter < startupTime) {
                    //do nothing, but keeps the flow straight
                } else if (meleeAbility.counter == startupTime) {
                    startActiveHitbox(meleeAbility, posComp, rotComp);
                } else if (meleeAbility.counter < startupTime + activeHitboxTime) {
                    duringActiveHitbox(meleeAbility, posComp, rotComp);
                } else if (meleeAbility.counter == startupTime + activeHitboxTime) {
                    endActiveHitbox(meleeAbility);
                } else if (meleeAbility.counter == startupTime + activeHitboxTime + endingLagTime) {
                    endExecuting(abComp, meleeAbility);
                }

            }
        }

        //cannot use else, because rechargeTime may be 0
        if (meleeAbility.counter == startupTime + activeHitboxTime + endingLagTime + rechargeTime) {
            endRecharge(meleeAbility);
        }

        meleeAbility.setRequestExecution(false); //checking if a request is made each frame.
    }


    private void startAbility(AbilityComp abComp, MeleeAbility meleeAbility) {
        abComp.setOccupiedBy(meleeAbility);
        meleeAbility.counter = 0;
    }

    private void startActiveHitbox(MeleeAbility meleeAbility, PositionComp chrPosComp, RotationComp charRotComp){
        int hbEnt = meleeAbility.getHitboxEntity();

        wc.activateEntity(hbEnt);

        //set hitbox direction
        float hitboxAngle = charRotComp.getAngle() +meleeAbility.getRelativeAngle();
        ((RotationComp)wc.getComponent(hbEnt, RotationComp.class)).setAngle(hitboxAngle);

        //set relative positiom
        duringActiveHitbox(meleeAbility, chrPosComp, charRotComp);
    }


    private void duringActiveHitbox(MeleeAbility meleeAbility, PositionComp charPosComp, RotationComp charRotCom ){
        int hbEnt = meleeAbility.getHitboxEntity();


        PositionComp hbPosComp = (PositionComp)wc.getComponent(hbEnt, PositionComp.class);

        Vec2 relPos = Vec2.newLenDir(meleeAbility.getRelativeDistance(), charRotCom.getAngle() + meleeAbility.getRelativeAngle() );
        hbPosComp.setPos( charPosComp.getPos().add(relPos) );

        //reset physics
        ((PhysicsComp)wc.getComponent(hbEnt, PhysicsComp.class) ).reset();
    }

    private void endActiveHitbox(MeleeAbility meleeAbility){
        //deactivate hitbox
        int hbEnt = meleeAbility.getHitboxEntity();
        wc.deactivateEntity(hbEnt);
    }

    private void endExecuting(AbilityComp abComp, MeleeAbility meleeAbility) {
        meleeAbility.setRecharging(true);
        abComp.setOccupiedBy(null); //release abComp
    }

    private void endRecharge(MeleeAbility meleeAbility){
        meleeAbility.setRecharging(false);

    }



}
