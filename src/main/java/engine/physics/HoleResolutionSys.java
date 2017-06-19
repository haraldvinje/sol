package engine.physics;

import engine.PositionComp;
import engine.Sys;
import engine.WorldContainer;
import javafx.geometry.Pos;
import utils.maths.Vec2;

import java.util.Set;

/**
 * Created by haraldvinje on 19-Jun-17.
 */
public class HoleResolutionSys implements Sys {
    private WorldContainer worldContainer;

    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.worldContainer = wc;
    }

    @Override
    public void update() {
        Set<Integer> holeEntities = worldContainer.getEntitiesWithComponentType(HoleComp.class);

        for (int entity: holeEntities){
            CollisionComp cc1 = (CollisionComp) worldContainer.getComponent(entity, CollisionComp.class);

            for (CollisionData data: cc1.getPrimaryCollisionDataList()){
                //resolve

                onCollision(data);
            }
            for (CollisionData data: cc1.getSecondaryCollisionDataList()){
                //resolve

                onCollision(data);
            }
        }
    }

    private void onCollision(CollisionData data) {
        respawnEntities(data);
        data.setActive(false);
    }

    private void respawnEntities(CollisionData data){
        //TODO: Make this method much more general
        PositionComp posComp = (PositionComp) worldContainer.getComponent(0, PositionComp.class);
        posComp.setPos(new Vec2(0,0));

    }



}
