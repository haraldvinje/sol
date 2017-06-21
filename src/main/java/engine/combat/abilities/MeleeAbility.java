package engine.combat.abilities;

import engine.PositionComp;
import engine.WorldContainer;
import engine.combat.DamagerComp;
import engine.graphics.ColoredMesh;
import engine.graphics.ColoredMeshComp;
import engine.graphics.ColoredMeshUtils;
import engine.physics.*;
import org.w3c.dom.css.Rect;

/**
 * Created by eirik on 19.06.2017.
 */
public class MeleeAbility {

    private int hitBoxEntity;
    private Shape hitBox;
    private boolean active;
    private float relativeDistance;
    private WorldContainer worldContainer;



    public MeleeAbility(WorldContainer wc){
        this(wc, null, 0.0f, false);
    }

    public MeleeAbility(WorldContainer wc, Shape hitBox, float relativeDistance){
        this(wc, hitBox, relativeDistance, false);
    }


    public MeleeAbility(WorldContainer wc, Shape hitBox, float relativeDistance, boolean active){
        if (hitBox instanceof Circle){
            initCircle(wc, (Circle)hitBox, relativeDistance, active);
        }
        if (hitBox instanceof Rectangle){
            initRectangle(wc, (Rectangle)hitBox, relativeDistance, active);
        }
    }



    private void initCircle(WorldContainer wc, Circle hitBox, float relativeDistance, boolean active){
        this.hitBoxEntity = wc.createEntity();
        this.hitBox = hitBox;
        this.active = active;
        this.relativeDistance = relativeDistance;
        this.worldContainer = wc;
        wc.addComponent(hitBoxEntity, new PositionComp(500,500));
        wc.addComponent(hitBoxEntity, new CollisionComp(hitBox));
        wc.addComponent(hitBoxEntity, new PhysicsComp());
        wc.addComponent(hitBoxEntity, new DamagerComp(2, 1));
        wc.addComponent(hitBoxEntity, new ColoredMeshComp(ColoredMeshUtils.createCircleMulticolor(hitBox.getRadius(), 8)));
    }

    private void initRectangle(WorldContainer wc, Rectangle hitBox, float relativeDistance, boolean active) {
        //TODO: Write rectanglemethod in meleeability
    }


    public int getHitBoxEntity() {
        return hitBoxEntity;
    }



    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public float getRelativeDistance() {
        return relativeDistance;
    }

    public void setRelativeDistance(float relativeDistance) {
        this.relativeDistance = relativeDistance;
    }

    public Shape getHitBox() {
        return hitBox;
    }

    public void setHitBox(Shape hitBox) {
        this.hitBox = hitBox;
    }
}
