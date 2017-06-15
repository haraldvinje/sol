package engine.character;

import engine.PositionComp;
import engine.Sys;
import engine.WorldContainer;

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


            float speed = 8;
            float stepX = ( (inputComp.isMoveRight()? 1:0) - (inputComp.isMoveLeft()? 1:0) ) * speed;
            float stepY = ( (inputComp.isMoveDown()? 1:0) - (inputComp.isMoveUp()? 1:0) ) * speed;
            posComp.addX( stepX );
            posComp.addY( stepY );
        }
    }
}
