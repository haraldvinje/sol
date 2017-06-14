package engine;

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


/*

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

*/


/*
    private void createCollisionEntitiesArray(){
        Set keySet = this.worldContainer.getCollisionComps().keySet();
        int size = keySet.size();
        this.collisionEntitiesArray = (Integer[]) keySet.toArray(new Integer[size]);
    }*/
/*
    public void update(){
        resolveCollisions();
    }*/



/*
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
*/

    public void update(){
        Integer[] cea = createCollisionEntitiesArray();
        int length = cea.length;
        for (int i = 0; i<length; i++){
            for (int j = i; j<length; j++){
                CollisionComp cc1 = worldContainer.getCollisionComponent(cea[i]);
                CollisionComp cc2 = worldContainer.getCollisionComponent(cea[j]);
                if (detectCollision(cc1.getShape(), cc2.getShape())){
                    System.out.println("kollisjon mellom to sirkler suuuuh :D \n Nå må det bare løses da hehe");
                    cc1.addCollidingCollisionComps(cc2);
                    cc1.addCollisionData(cc2);
                    cc2.addCollisionData(cc1);

                }
            }
        }
    }

    private Integer[] createCollisionEntitiesArray(){
        Set keySet = this.worldContainer.getCollisionComps().keySet();
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
        return centerDist<=rsum*rsum;
    }
}
