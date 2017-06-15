package engine.physics;

import engine.Sys;
import engine.WorldContainer;
import engine.maths.Vec2;

/**
 * Created by haraldvinje on 14-Jun-17.
 */
public class CollisionResolutionSys implements Sys {

    private WorldContainer worldContainer;

    public CollisionResolutionSys(){

    }

    public CollisionResolutionSys(WorldContainer wc){
        this.worldContainer = wc;
    }



    @Override
    public void update() {
        //need to get all collisionComponents
        //run over every collisionDataList for every collisionComponent
        //add vector to velocitycomponent of based on ID
        //success
    }





}
