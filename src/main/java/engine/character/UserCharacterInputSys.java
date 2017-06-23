package engine.character;

import engine.Sys;
import engine.UserInput;
import engine.WorldContainer;

/**
 * Created by eirik on 15.06.2017.
 */
public class UserCharacterInputSys implements Sys {


    private WorldContainer wc;

    private UserInput userInput;


    public UserCharacterInputSys(UserInput userInput) {
        this.userInput = userInput;
    }

    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.wc = wc;
    }

    @Override
    public void update() {

        for (int entity : wc.getEntitiesWithComponentType(UserCharacterInputComp.class)) {

            CharacterInputComp inputComp = (CharacterInputComp)wc.getComponent(entity, CharacterInputComp.class);


            inputComp.setMoveLeft(userInput.isKeyboardPressed(UserInput.KEY_A));
            inputComp.setMoveRight(userInput.isKeyboardPressed(UserInput.KEY_D));
            inputComp.setMoveUp(userInput.isKeyboardPressed(UserInput.KEY_W));
            inputComp.setMoveDown(userInput.isKeyboardPressed(UserInput.KEY_S));

            inputComp.setAction1(userInput.isMousePressed(UserInput.MOUSE_BUTTON_1));
            inputComp.setAction2(userInput.isMousePressed(UserInput.MOUSE_BUTTON_2));

            inputComp.setAimX(userInput.getMouseX());
            inputComp.setAimY(userInput.getMouseY());

        }
    }

    @Override
    public void terminate() {

    }
}
