package engine.character;

import engine.*;
import engine.maths.Vec2;
import engine.physics.PhysicsComp;
import utils.maths.TrigUtils;

/**
 * Created by eirik on 15.06.2017.
 */
public class CharacterSys implements Sys {


    private WorldContainer wc;


    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.wc = wc;
    }

    @Override
    public void update() {
        //float aimAngle = TrigUtils.pointDirection(posComp.getX(), posComp.getY(),   userInput.getMouseX(), userInput.getMouseY());

        for (int entity : wc.getEntitiesWithComponentType(CharacterComp.class)) {
            PositionComp posComp = (PositionComp) wc.getComponent(entity, PositionComp.class);
            CharacterInputComp inputComp = (CharacterInputComp) wc.getComponent(entity, CharacterInputComp.class);
            RotationComp rotComp = (RotationComp) wc.getComponent(entity, RotationComp.class);
            PhysicsComp phComp = (PhysicsComp) wc.getComponent(entity, PhysicsComp.class);

            updateEntity(entity, posComp, inputComp, rotComp, phComp);
        }
    }

    private void updateEntity(int entity, PositionComp posComp, CharacterInputComp inputComp, RotationComp rotComp, PhysicsComp phComp) {
        updateMove(inputComp, phComp);
        updateRotation(inputComp, posComp, rotComp);
    }


    private void updateMove(CharacterInputComp inputComp, PhysicsComp phComp) {
        float speed = 8;
        float stepX = ( (inputComp.isMoveRight()? 1:0) - (inputComp.isMoveLeft()? 1:0) ) * speed;
        float stepY = ( (inputComp.isMoveDown()? 1:0) - (inputComp.isMoveUp()? 1:0) ) * speed;
        phComp.addAcceleration(new Vec2(stepX, stepY));
    }

    private void updateRotation(CharacterInputComp inputComp, PositionComp posComp, RotationComp rotComp) {
        float angle = TrigUtils.pointDirection(posComp.getX(), posComp.getY(), inputComp.getAimX(), inputComp.getAimY());
        rotComp.setAngle(angle);
    }
}
