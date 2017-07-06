package engine.character;

import engine.*;
import engine.combat.DamageableComp;
import engine.combat.DamagerComp;
import engine.combat.abilities.Ability;
import engine.combat.abilities.AbilityComp;
import engine.graphics.ColoredMesh;
import engine.graphics.ColoredMeshComp;
import engine.graphics.ColoredMeshUtils;
import engine.graphics.text.TextMeshComp;
import engine.physics.*;
import game.GameUtils;
import utils.maths.M;
import utils.maths.TrigUtils;
import utils.maths.Vec2;
import utils.maths.Vec4;

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

        int charNumb = 0;

        for (int entity : wc.getEntitiesWithComponentType(CharacterComp.class)) {

            updateEntity(entity, charNumb);

            charNumb++;
        }
    }

    @Override
    public void terminate() {

    }

    private void updateEntity(int entity, int charNumb) {
        CharacterComp charComp = (CharacterComp) wc.getComponent(entity, CharacterComp.class);
        PositionComp posComp = (PositionComp) wc.getComponent(entity, PositionComp.class);
        CharacterInputComp inputComp = (CharacterInputComp) wc.getComponent(entity, CharacterInputComp.class);
        RotationComp rotComp = (RotationComp) wc.getComponent(entity, RotationComp.class);
        PhysicsComp phComp = (PhysicsComp) wc.getComponent(entity, PhysicsComp.class);
        AbilityComp abComp = (AbilityComp) wc.getComponent(entity, AbilityComp.class);
        DamageableComp dmgableComp = (DamageableComp)wc.getComponent(entity, DamageableComp.class);
        AffectedByHoleComp affholeComp = (AffectedByHoleComp)wc.getComponent(entity, AffectedByHoleComp.class);
        TextMeshComp textComp = (TextMeshComp)wc.getComponent(entity, TextMeshComp.class);



        checkHoleAffected(charNumb, posComp, phComp, charComp, dmgableComp, affholeComp);

        //do not take input if character is executing ability or is stunned
        if (abComp.getOccupiedBy() != null) return;
        if (dmgableComp.isStunned()) return;

        updateMove(charComp, inputComp, phComp);
        updateRotation(inputComp, posComp, rotComp);
        updateAbilities(charComp, abComp, inputComp, posComp, rotComp);
        updateDisplayDamage(charNumb, dmgableComp, textComp);
    }

    private void checkHoleAffected(int charNumb, PositionComp posComp, PhysicsComp physComp, CharacterComp charComp, DamageableComp dmgablComp, AffectedByHoleComp affholeComp) {
        if (affholeComp.isHoleAffectedFlag()) {

            Vec2 respawnPos = (charNumb == 0)? new Vec2(GameUtils.MAP_WIDTH/4f, GameUtils.MAP_HEIGHT/2f) : new Vec2(GameUtils.MAP_WIDTH*3f/4f, GameUtils.MAP_HEIGHT/2f);

            dmgablComp.reset();
            physComp.reset();

            charComp.incrementRespawnCount();

            posComp.setPos(respawnPos);

            System.out.println("Character numb: "+charNumb+" stocks lost: "+charComp.getRespawnCount());
        }
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

        if (inputComp.isAction1()){
            abComp.requestExecution(0);
        }

        if (inputComp.isAction2()) {
            abComp.requestExecution(1);
        }

        if (inputComp.isAction3()) {
            abComp.requestExecution(2);
        }


    }

    int aaa = 0;
    private void updateDisplayDamage(int charNumb, DamageableComp dmgablComp, TextMeshComp textComp) {
        Vec2[] pos = {new Vec2(50, GameUtils.MAP_HEIGHT/2),
            new Vec2(GameUtils.MAP_WIDTH-150, GameUtils.MAP_HEIGHT/2) };

        textComp.setSize( (aaa++) % 60 == 0? (int)(M.random()*72*2) : textComp.getSize() );
        float sizeNormalized = textComp.getSize()/ (72*2);
        textComp.setColor(new Vec4(sizeNormalized, 1- sizeNormalized, sizeNormalized*sizeNormalized, 1));

        textComp.setViewX(pos[charNumb].x);
        textComp.setViewY(pos[charNumb].y);
        textComp.getTextMesh().setString( "Ahaha\n"+Float.toString(textComp.getSize()) + "\non another line" );
    }


}
