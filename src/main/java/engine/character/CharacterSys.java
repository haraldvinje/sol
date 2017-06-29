package engine.character;

import engine.*;
import engine.combat.DamageableComp;
import engine.combat.DamagerComp;
import engine.combat.abilities.Ability;
import engine.combat.abilities.AbilityComp;
import engine.graphics.ColoredMesh;
import engine.graphics.ColoredMeshComp;
import engine.graphics.ColoredMeshUtils;
import engine.physics.*;
import utils.maths.TrigUtils;
import utils.maths.Vec2;

/**
 * Created by eirik on 15.06.2017.
 */
public class CharacterSys implements Sys {


    private WorldContainer wc;




    public CharacterSys() {

    }


    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.wc = wc;

        //allocate bullet entity

    }

    @Override
    public void update() {
        //float aimAngle = TrigUtils.pointDirection(posComp.getX(), posComp.getY(),   userInput.getMouseX(), userInput.getMouseY());

        for (int entity : wc.getEntitiesWithComponentType(CharacterComp.class)) {
            CharacterComp charComp = (CharacterComp) wc.getComponent(entity, CharacterComp.class);
            PositionComp posComp = (PositionComp) wc.getComponent(entity, PositionComp.class);
            CharacterInputComp inputComp = (CharacterInputComp) wc.getComponent(entity, CharacterInputComp.class);
            RotationComp rotComp = (RotationComp) wc.getComponent(entity, RotationComp.class);
            PhysicsComp phComp = (PhysicsComp) wc.getComponent(entity, PhysicsComp.class);
            AbilityComp abComp = (AbilityComp) wc.getComponent(entity, AbilityComp.class);
            DamageableComp dmgableComp = (DamageableComp)wc.getComponent(entity, DamageableComp.class);

            updateEntity(entity, charComp, abComp, posComp, inputComp, rotComp, phComp, dmgableComp);
        }
    }

    @Override
    public void terminate() {

    }

    private void updateEntity(int entity, CharacterComp charComp, AbilityComp abComp, PositionComp posComp, CharacterInputComp inputComp, RotationComp rotComp, PhysicsComp phComp, DamageableComp dmgableComp) {
        //do not take input if character is executing ability or is stunned
        if (abComp.getOccupiedBy() != null) return;
        if (dmgableComp.isStunned()) return;

        updateMove(charComp, inputComp, phComp);
        updateRotation(inputComp, posComp, rotComp);
        updateAbilities(charComp, abComp, inputComp, posComp, rotComp);
    }


    private void updateMove(CharacterComp charComp, CharacterInputComp inputComp, PhysicsComp phComp) {
        float accel = charComp.getMoveAccel();
        float stepX = ( (inputComp.isMoveRight()? 1:0) - (inputComp.isMoveLeft()? 1:0) );
        float stepY = ( (inputComp.isMoveDown()? 1:0) - (inputComp.isMoveUp()? 1:0) );

        phComp.addAcceleration(new Vec2(stepX, stepY).normalize().scale(accel));
    }

    private void updateRotation(CharacterInputComp inputComp, PositionComp posComp, RotationComp rotComp) {
        float newAngle = TrigUtils.pointDirection(posComp.getX(), posComp.getY(), inputComp.getAimX(), inputComp.getAimY());
        float diffAngle = TrigUtils.shortesAngleBetween(rotComp.getAngle(), newAngle);

        //add a portion of diffAngle
        rotComp.addAngle(diffAngle * 0.2f);
    }

    private void updateAbilities(CharacterComp charComp, AbilityComp abComp, CharacterInputComp inputComp, PositionComp posComp, RotationComp rotComp) {
        //System.out.println(inputComp.isAction1());

        if (inputComp.isAction2()) {
            abComp.requestExecution(0);
        }

        if (inputComp.isAction1()){
            abComp.requestExecution(1);
        }


    }


}
