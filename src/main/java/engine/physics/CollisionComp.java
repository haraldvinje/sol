package engine.physics;

import engine.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haraldvinje on 13-Jun-17.
 */
public class CollisionComp implements Component {

    private Shape shape;
    private List<CollisionComp> collidingCollisionComps = new ArrayList<CollisionComp>();
    private List<CollisionData> collisionData = new ArrayList<CollisionData>();


    public CollisionComp(Shape s) {
        this.shape = s;
    }

    public Shape getShape() {
        return shape;
    }

    public void addCollisionData(CollisionComp other){

    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public void addCollidingCollisionComps(CollisionComp other){
        collidingCollisionComps.add(other);
    }

    public void reset(){
        collidingCollisionComps.clear();
        collisionData.clear();
    }
}
