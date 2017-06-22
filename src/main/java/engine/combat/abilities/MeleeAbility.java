package engine.combat.abilities;

import engine.Component;
import engine.PositionComp;
import engine.WorldContainer;
import engine.character.UserCharacterInputComp;
import engine.combat.DamagerComp;
import engine.graphics.ColoredMesh;
import engine.graphics.ColoredMeshComp;
import engine.graphics.ColoredMeshUtils;
import engine.physics.*;
import org.w3c.dom.css.Rect;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

/**
 * Created by eirik on 19.06.2017.
 */
public class MeleeAbility {

    private int hitboxEntity;
    private Shape hitbox;
    private boolean active = false;
    private float relativeDistance;
    private WorldContainer worldContainer;
//  private List<Component> components = new ArrayList<>(5);
    private CollisionComp collComp;



    private boolean executable = true;



    public int durationCounter;
    public int rechargeTimeCounter;


    private final int startupTime = 10;
    private final int activeHitboxTime = 20;
    private final int endlagTime = 10;
    private final int duration = startupTime+activeHitboxTime+endlagTime;
    private final int rechargeTime = 20;
    private ColoredMeshComp colMeshComp;


    public MeleeAbility(WorldContainer wc){
        this(wc, null, 0.0f);
    }



    public MeleeAbility(WorldContainer wc, Shape hitbox, float relativeDistance){
        if (hitbox instanceof Circle){
            createCircleHitbox(wc, (Circle)hitbox, relativeDistance);
        }
        if (hitbox instanceof Rectangle){
            createRectangleHitbox(wc, (Rectangle)hitbox, relativeDistance);
        }
    }



    public void requestExecution() {
        if (!isExecutable()) {
            return;
        }
        execute();
    }

    private void execute() {
        durationCounter = 0;
        setExecutable(false);
        setActiveHitbox(true);
    }

    public boolean isExecutable() {
        return executable;
    }

    public void setExecutable(boolean executable) {
        this.executable = executable;
    }


    public void setActiveHitbox(boolean active) {
        this.active = active;
        if (active){
            activateComponents();
        }
        else {
            deactivateComponents();
        }
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
    }



    public boolean isActiveHitbox() {
        return active;
    }

    public void startRechargeTime() {
        rechargeTimeCounter = 0;
    }


    public int getDuration() {
        return duration;
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

    public void setRelativeDistance(float relativeDistance) {
        this.relativeDistance = relativeDistance;
    }

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
        addComponent(new PositionComp(500,500));
        //Adding collisionComp manually. The only component to be deactivated when meleeability is not active.
        CollisionComp collisionComp = new CollisionComp(hitbox);
        addComponent(collisionComp);
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
