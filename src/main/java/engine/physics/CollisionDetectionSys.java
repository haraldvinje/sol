package engine.physics;

import engine.PositionComp;
import engine.Sys;
import engine.WorldContainer;
import engine.maths.Vec2;

import java.util.Arrays;
import java.util.Set;

/**
 * Created by haraldvinje on 13-Jun-17.
 */
public class CollisionDetectionSys implements Sys {


/*
    private Integer[] collisionEntitiesArray;
*/

    private WorldContainer worldContainer;

    public CollisionDetectionSys(){}

    public CollisionDetectionSys(WorldContainer wc){
        this.worldContainer = wc;
    }


    @Override
    public void setWorldContainer(WorldContainer wc) {
        //assign wc
    }

    public void update(){
        Integer[] cea = createCollisionEntitiesArray();
        System.out.println(Arrays.toString(cea));
        int length = cea.length;
        for (int i = 0; i<length; i++){
            for (int j = i+1; j<length; j++){
                CollisionComp cc1 = (CollisionComp)worldContainer.getComponent(cea[i], CollisionComp.class);
                CollisionComp cc2 = (CollisionComp)worldContainer.getComponent(cea[j], CollisionComp.class);
                PositionComp po1 = (PositionComp)worldContainer.getComponent(cea[i], PositionComp.class);
                PositionComp po2 = (PositionComp)worldContainer.getComponent(cea[j], PositionComp.class);
                Shape s1 = cc1.getShape();
                Shape s2 = cc2.getShape();

                s1.setXY(po1.getX(), po1.getY());
                s2.setXY(po2.getX(), po2.getY());

                if (detectCollision(cc1.getShape(), cc2.getShape())){
                    System.out.println("kollisjon mellom to sirkler suuuuh :D \n Nå må det bare løses da hehe");
                    cc1.addCollidingEntity(cea[j]);
                    CollisionData collisionData = new CollisionData(cea[i], cea[j]);
                    calculateCollisionData(collisionData);
                    cc1.addCollisionData(collisionData);
                }
            }
        }
    }

    private Integer[] createCollisionEntitiesArray(){
        Set keySet = this.worldContainer.getComponentsOfType(CollisionComp.class).keySet();
        int size = keySet.size();
        return (Integer[]) keySet.toArray(new Integer[size]);
    }

    public boolean detectCollision(Shape s1, Shape s2){
        if (s1 instanceof Circle && s2 instanceof Circle){
            return detectCollision((Circle)s1,(Circle)s2);
        }
        return false;
    }

    private boolean detectCollision(Circle circle1, Circle circle2){
        float c1x = circle1.getX();
        float c1y = circle1.getY();
        float c1r = circle1.getRadius();

        float c2x = circle2.getX();
        float c2y = circle2.getY();
        float c2r = circle2.getRadius();

        float dx = c2x-c1x;
        float dy = c2y-c1y;

        float rsum = c1r+c2r;

        //The distance between the center of two circles. No square root to save computation time
        float centerDist =  dx*dx + dy*dy;


        //need to sqaure the sum of the radiuses to compare it with distance squared
        return centerDist<=rsum*rsum;
    }


    private void calculateCollisionData(CollisionData collisionData){
        Shape s1 = ((CollisionComp)worldContainer.getComponent(collisionData.getEntity1(), CollisionComp.class)).getShape();
        Shape s2 = ((CollisionComp)worldContainer.getComponent(collisionData.getEntity2(), CollisionComp.class)).getShape();

        if (s1 instanceof Circle && s2 instanceof Circle){
            calculateCollisionDataCircCirc((Circle) s1, (Circle) s2, collisionData);
        }
    }

    private void calculateCollisionDataCircCirc(Circle c1, Circle c2, CollisionData collisionData){

        Vec2 colVector = new Vec2(c1.getX()-c2.getX(), c1.getY()-c2.getY() );
        float maxDist = c1.getRadius() + c2.getRadius();
        float dist = colVector.getLength();

        collisionData.setCollisionVector(colVector.scale(1/dist));
        collisionData.setPenetrationDepth(maxDist-dist);
    }
}
