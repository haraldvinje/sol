package engine.character;

import engine.*;
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

            updateEntity(entity, posComp, inputComp, rotComp);
        }
    }

    private void updateEntity(int entity, PositionComp posComp, CharacterInputComp inputComp, RotationComp rotComp) {
        updateMove(inputComp, posComp);
        updateRotation(inputComp, posComp, rotComp);
    }


    private void updateMove(CharacterInputComp inputComp, PositionComp posComp) {
        float speed = 8;
        float stepX = ( (inputComp.isMoveRight()? 1:0) - (inputComp.isMoveLeft()? 1:0) ) * speed;
        float stepY = ( (inputComp.isMoveDown()? 1:0) - (inputComp.isMoveUp()? 1:0) ) * speed;
        posComp.addX( stepX );
        posComp.addY( stepY );
    }

    private void updateRotation(CharacterInputComp inputComp, PositionComp posComp, RotationComp rotComp) {
        float angle = TrigUtils.pointDirection(posComp.getX(), posComp.getY(), inputComp.getAimX(), inputComp.getAimY());
        rotComp.setAngle(angle);
    }
}
