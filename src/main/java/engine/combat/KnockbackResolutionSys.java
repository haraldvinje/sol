package engine.combat;

import engine.Sys;
import engine.WorldContainer;

/**
 * Created by eirik on 19.06.2017.
 */
public class KnockbackResolutionSys implements Sys {


    private WorldContainer wc;



    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.wc = wc;
    }

    @Override
    public void update() {

        for (int entity : wc.getEntitiesWithComponentType(DamageableComp.class)) {



        }

    }
}
