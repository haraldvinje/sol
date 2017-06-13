package engine;

import javax.swing.text.Position;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by eirik on 13.06.2017.
 *
 * Contains all entities and components
 */
public class WorldContainer {

    private static int ENTITY_COUNT = 32;

    public static final int COMPMASK_ENTITY_EXISTS = 1 << 0,
                            COMPMASK_POSITION = 1 << 1,
                            COMPMASK_VELOCITY = 1 << 2,
                            COMPMASK_COLLISION = 1 << 3;




    private int[] entityMask; //the main container for entities

    private Map<Integer, PositionComp> positionComps = new HashMap<Integer, PositionComp>();

    private Map<Integer, VelocityComp> velocityComps = new HashMap<Integer, VelocityComp>();

    private Map<Integer, CollisionComp> collisionComps = new HashMap<Integer, CollisionComp>();

    private MechanicsSystem mechSystem;



    public WorldContainer() {
        entityMask = new int[ENTITY_COUNT]; //all bits set to 0; no entity se

    }

    public void init(){

        mechSystem = new MechanicsSystem(this);
        mechSystem.init();

    }

    public void updateSystems(){
        mechSystem.updateComponents();
    }



    public Map<Integer, PositionComp> getPositionComps() {
        return positionComps;
    }

    public Map<Integer, VelocityComp> getVelocityComps() {
        return velocityComps;
    }

    public Map<Integer, CollisionComp> getCollisionComps() {
        return collisionComps;
    }

    public MechanicsSystem getMechanicsSystem(){
        return this.mechSystem;
    }


    public int createEntity() {
        for (int i = 0; i < ENTITY_COUNT; i++) {
            System.out.println(entityExists(i));
            if (!entityExists(i)) {
                resetEntity(i);
                return i;
            }
        }
        throw new IllegalStateException("There is not allocated enough space for more entities");
    }

    public void createPositionComp(int entity, float x, float y) {
        PositionComp pc = new PositionComp();
        pc.setX(x);
        pc.setY(y);
        positionComps.put(entity, pc);
    }

    public void createVelocityComp(int entity, float vx, float vy){
        VelocityComp vc = new VelocityComp();
        vc.setVx(vx);
        vc.setVy(vy);
        velocityComps.put(entity, vc);
    }

    public void createCollisionComp(int entity, Shape shape){
        CollisionComp cc = new CollisionComp();
        cc.setShape(shape);
        collisionComps.put(entity, cc);
    }


    public PositionComp getPositionComponent(int entity) {return positionComps.get(entity);}

    public VelocityComp getVelocityComponent(int entity) {return velocityComps.get(entity);}

    public CollisionComp getCollisionComponent(int entity) {return collisionComps.get(entity);}

    public boolean entityExists(int entity) {
        return hasComponent(entity, COMPMASK_ENTITY_EXISTS);
    }



    private boolean hasComponent(int entity, int compmask) {
        return (entityMask[entity] & compmask) == compmask;
    }

    private void resetEntity(int entity) { //cleans entity masks ++
        entityMask[entity] = 0;
    }
}
