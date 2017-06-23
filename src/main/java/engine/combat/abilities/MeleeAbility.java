package engine.combat.abilities;

import engine.Component;
import engine.PositionComp;
import engine.RotationComp;
import engine.WorldContainer;
import engine.character.UserCharacterInputComp;
import engine.combat.DamagerComp;
import engine.graphics.ColoredMesh;
import engine.graphics.ColoredMeshComp;
import engine.graphics.ColoredMeshUtils;
import engine.physics.*;
import org.w3c.dom.css.Rect;
import utils.maths.Vec2;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

/**
 * Created by eirik on 19.06.2017.
 */
public class MeleeAbility extends Ability{

    private int hitboxEntity;
    private Shape hitbox;
    private boolean activeHitbox = false;

    private float relativeDistance;
    private float relativeAngle;

    private WorldContainer worldContainer;
//  private List<Component> components = new ArrayList<>(5);
    private CollisionComp collComp;
    private PositionComp posComp;


    private boolean requestExecution;
    private boolean executing = false;



    public int counter;



    private int startupTime;
    private int activeHitboxTime;
    private int endlagTime;
    private int rechargeTime;
    private ColoredMeshComp colMeshComp;
    private RotationComp rotComp;


    public MeleeAbility(WorldContainer wc){
        this(wc, null, 0.0f, 0.0f, 10, 10, 10, 10);
    }



    public MeleeAbility(WorldContainer wc, Shape hitbox, float relativeDistance, float relativeAngle, int startupTime, int activeHitboxTime, int endlagTime, int rechargeTime){
        this.startupTime = startupTime;
        this.activeHitboxTime = activeHitboxTime;
        this.endlagTime = endlagTime;
        this.rechargeTime = rechargeTime;
        this.relativeDistance = relativeDistance;
        this.relativeAngle = relativeAngle;

        if (hitbox instanceof Circle){
            createCircleHitbox(wc, (Circle)hitbox, relativeDistance);
        }

        if (hitbox instanceof Rectangle){
            createRectangleHitbox(wc, (Rectangle)hitbox, relativeDistance);
        }
    }


    public boolean isRequestingExecution() {
        return requestExecution;
    }

    public void requestExecution() {
        if (isExecuting()) {
            return;
        }
        requestExecution = true;

    }

    public void setRequestExecution(boolean b){
        this.requestExecution = b;
    }



    public void execute() {
        counter = 0;
        setExecuting(true);
    }

    public int getStartupTime() {
        return startupTime;
    }

    public int getActiveHitboxTime() {
        return activeHitboxTime;
    }

    public int getEndlagTime() {
        return endlagTime;
    }


    public void setExecuting(boolean status){
        this.executing = status;
    }

    public boolean isExecuting(){
        return this.executing;
    }


    public void setActiveHitbox(boolean active) {
        if (!this.activeHitbox && active){
            activateComponents();
        }
        else if (this.activeHitbox && !active){
            deactivateComponents();
        }
        this.activeHitbox = active;
    }

    public void setPosition(Vec2 vector){
        this.posComp.setPos(vector);
    }

    public void setAngle(float angle){
        this.rotComp.setAngle(angle);
    }



    private void deactivateComponents(){
        worldContainer.removeComponent(hitboxEntity, CollisionComp.class);
        worldContainer.removeComponent(hitboxEntity, ColoredMeshComp.class);
        /*for (Component c: components){
            worldContainer.removeComponent(hitboxEntity, c.getClass());
        }*/


    }

    private void activateComponents(){
        worldContainer.addComponent(hitboxEntity, collComp);
        worldContainer.addComponent(hitboxEntity, colMeshComp);
        PhysicsComp phComp = (PhysicsComp) worldContainer.getComponent(hitboxEntity, PhysicsComp.class);
        phComp.resetVelocity();


    }



    public boolean isActiveHitbox() {
        return activeHitbox;
    }



    public int getRechargeTime() {
        return rechargeTime;
    }

    public int getHitboxEntity() {
        return hitboxEntity;
    }




    public float getRelativeDistance() {
        return relativeDistance;
    }

    public float getRelativeAngle() {return relativeAngle;}

    public Shape getHitbox() {
        return hitbox;
    }

    public void setHitbox(Shape hitbox) {
        this.hitbox = hitbox;
    }






    private void addComponent(Component component){
        //components.add(component);
        worldContainer.addComponent(hitboxEntity,component);
        worldContainer.addComponent(hitboxEntity, component);
    }



    private void createCircleHitbox(WorldContainer wc, Circle hitbox, float relativeDistance){
        this.hitboxEntity = wc.createEntity();
        this.hitbox = hitbox;
        this.relativeDistance = relativeDistance;
        this.worldContainer = wc;


        //Adding positionComp manually to reach when hitbox is active;
        PositionComp posComp = new PositionComp(0,0);
        this.posComp = posComp;
        addComponent(posComp);

        RotationComp rotComp = new RotationComp();
        addComponent(rotComp);
        this.rotComp = rotComp;

        //Adding collisionComp manually. The only component to be deactivated when meleeability is not active.
        CollisionComp collisionComp = new CollisionComp(hitbox);
        //addComponent(collisionComp);
        this.collComp = collisionComp;

        //ColoredMeshComponent stored in object. Removing when inactive hitbox
        float[] red = {1.0f, 0f,0f};
        ColoredMeshComp colMeshComp = new ColoredMeshComp(ColoredMeshUtils.createCircleSinglecolor(hitbox.getRadius(), 8, red));
        //addComponent(colMeshComp);
        this.colMeshComp = colMeshComp;

        //adding relevant components. These will stay on hitbox at all times
        addComponent(new PhysicsComp());
        addComponent(new DamagerComp(2, 1));



    }

    private void createRectangleHitbox(WorldContainer wc, Rectangle hitbox, float relativeDistance) {
        //TODO: Write rectanglemethod in meleeability
    }


}
