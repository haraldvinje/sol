package engine;

import engine.character.CharacterComp;
import engine.combat.DamageableComp;
import engine.graphics.ColoredMeshComp;
import engine.graphics.ColoredMeshUtils;
import engine.graphics.ViewRenderComp;
import engine.graphics.text.Font;
import engine.graphics.text.FontType;
import engine.graphics.text.TextMesh;
import engine.graphics.text.TextMeshComp;
import engine.network.CharacterStateData;
import utils.maths.Vec4;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eirik on 09.07.2017.
 */
public class OnScreenSys implements Sys{


    private WorldContainer wc;


    private int botBarEnt;
    private List<Integer> damageTexts = new ArrayList<>();


    public OnScreenSys(WorldContainer wc, int characterCount) {

        //create bottom bar
        //botBarEnt = createBotBarEnt(wc);

        //create character damage displays
        int halfCharCount = characterCount /2;
        Vec4 textColor = new Vec4(0.9f, 0, 0, 1);
        for (int i = 0; i < characterCount; i++) {

            float x = i < halfCharCount ? 100 : wc.getView().getWidth() - 200;
            float y = wc.getView().getHeight()-200 + 64f * (i % halfCharCount);
            int textEnt = createTextEntty(wc, "0", 64, x, y, textColor);
            damageTexts.add( textEnt );
        }
    }

    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.wc = wc;
    }

    @Override
    public void update() {

        int charNumb = 0;
        for (int entity : wc.getEntitiesWithComponentType(CharacterComp.class)) {
            DamageableComp dmgableComp = (DamageableComp)wc.getComponent(entity, DamageableComp.class);

            setTextString(damageTexts.get(charNumb), Integer.toString((int)dmgableComp.getDamage()));

            charNumb++;
        }
    }

    @Override
    public void terminate() {

    }


    private int createTextEntty(WorldContainer wc, String s, float size, float x, float y, Vec4 color) {
        int t = wc.createEntity();
        wc.addComponent(t, new PositionComp(x, y));
        wc.addComponent(t, new ViewRenderComp( new TextMesh( s, Font.getFont(FontType.BROADWAY),size, color) ) );

        return t;
    }

    private void setTextString(int entity, String s) {
        ViewRenderComp viewrendComp = (ViewRenderComp) wc.getComponent(entity, ViewRenderComp.class);
        viewrendComp.getTextMesh(0).setString(s);
    }

    private int createBotBarEnt(WorldContainer wc) {
        float viewW = wc.getView().getWidth();
        float viewH = wc.getView().getHeight();

        float barHeight = 128;

        int b = wc.createEntity();
        wc.addComponent(b, new PositionComp(0, viewH-barHeight));
        wc.addComponent(b, new ViewRenderComp(ColoredMeshUtils.createRectangle(viewW, barHeight)) );

        return b;
    }
}
