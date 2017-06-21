package engine.combat.abilities;

import engine.PositionComp;
import engine.RotationComp;
import engine.Sys;
import engine.WorldContainer;
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
        Set<Integer> abilityComps = worldContainer.getEntitiesWithComponentType(AbilityComp.class);
        for (int entity: abilityComps){
            AbilityComp abComp = (AbilityComp) worldContainer.getComponent(entity, AbilityComp.class);
            PositionComp posComp = (PositionComp) worldContainer.getComponent(entity, PositionComp.class);
            RotationComp rotComp = (RotationComp) worldContainer.getComponent(entity, RotationComp.class);
            float x = posComp.getX();
            float y = posComp.getY();
            float angle = rotComp.getAngle();
            for (MeleeAbility meleeAbility: abComp.getMeleeAbilities()){

                int hitBoxEntity = meleeAbility.getHitBoxEntity();

                PositionComp mPosComp = (PositionComp) worldContainer.getComponent(hitBoxEntity, PositionComp.class);

                Vec2 vector = Vec2.newLenDir(meleeAbility.getRelativeDistance(), angle);
                Vec2 result = new Vec2(x,y).add(vector);
                mPosComp.setPos(result);

            }

        }
    }
}
