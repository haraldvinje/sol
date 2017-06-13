package engine;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by haraldvinje on 13-Jun-17.
 */
public class MechanicsSystem {


    private Integer[] collisionEntitiesArray;

    private WorldContainer worldContainer;

    public MechanicsSystem(WorldContainer wc){
        this.worldContainer = wc;
        init();
    }



    public void init(){
        createCollisionEntitiesArray();
        updateComponents();
    }

    public void updateComponents() {
        int length = this.collisionEntitiesArray.length;
        Integer[] cea = this.collisionEntitiesArray;


        for (int i = 0; i<length; i++){
            ((Circle) worldContainer.getCollisionComponent(cea[i]).getShape()).setCx(worldContainer.getPositionComponent(cea[i]).getX());
            ((Circle) worldContainer.getCollisionComponent(cea[i]).getShape()).setCy(worldContainer.getPositionComponent(cea[i]).getY());

        }
    }



    /*
    public void setComponents(Map<Integer, PositionComp> positionComps, Map<Integer, VelocityComp> velocityComps, Map<Integer, CollisionComp> collisionComps){
        this.positionComps = positionComps;
        this.velocityComps = velocityComps;
        this.collisionComps = collisionComps;
    }
    */

    private void createCollisionEntitiesArray(){
        Set keySet = this.worldContainer.getCollisionComps().keySet();
        int size = keySet.size();
        this.collisionEntitiesArray = (Integer[]) keySet.toArray(new Integer[size]);
    }

    public void update(){
        resolveCollisions();
        updateComponents();
    }



    public void resolveCollisions(){
        int length = this.collisionEntitiesArray.length;
        Integer[] cea = this.collisionEntitiesArray;

        for (int i = 0; i<length; i++){
            for (int j = i; j<length; j++){
                if (detectCollision(worldContainer.getCollisionComponent(cea[i]).getShape(), worldContainer.getCollisionComponent(cea[j]).getShape())){
                    System.out.println("kollisjon mellom to sirkler suuuuh :D \n Nå må det bare løses da hehe");
                }
            }
        }
    }

    public boolean detectCollision(Shape s1, Shape s2){
        if (s1 instanceof Circle && s2 instanceof Circle){
            return detectCollision((Circle)s1,(Circle)s2);
        }
        return false;
    }

    public boolean detectCollision(Circle circle1, Circle circle2){
        float c1x = circle1.getCx();
        float c1y = circle1.getCy();
        float c1r = circle1.getRadius();

        float c2x = circle2.getCx();
        float c2y = circle2.getCy();
        float c2r = circle2.getRadius();

        float dx = c2x-c1x;
        float dy = c2y-c1y;

        float rsum = c1r+c2r;

        //The distance between the center of two circles. No square root to save computation time
        float centerDist =  dx*dx + dy*dy;


        //need to sqaure the sum of the radiuses to compare it with distance squared
        return centerDist<rsum*rsum;
    }

}
