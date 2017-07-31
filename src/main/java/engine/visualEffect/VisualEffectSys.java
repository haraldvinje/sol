package engine.visualEffect;

import engine.Sys;
import engine.WorldContainer;
import engine.graphics.ColoredMesh;
import engine.graphics.RenderSys;
import engine.graphics.TexturedMesh;
import engine.graphics.view_.View;
import utils.maths.Mat4;
import utils.maths.Vec2;
import utils.maths.Vec3;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Created by eirik on 06.07.2017.
 */
public class VisualEffectSys implements Sys{

    private WorldContainer wc;

    private static List<VisualEffect> effectsRunning = new ArrayList<>();
    private LinkedList<VisualEffect> removeEffects = new LinkedList<>();

    private Mat4 viewTransform, projectionTransform;


    public static void forEachActiveParticle(Consumer<? super Particle> consumer) {
        effectsRunning.forEach(e -> e.activeParticleStream().forEach(consumer));
    }

    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.wc = wc;
    }

    @Override
    public void update() {
        //get view
        View view = wc.getView();
        viewTransform = view.getViewTransform();
        projectionTransform = view.getProjectionTransform();

        //get new effects to run
        for (int entity : wc.getEntitiesWithComponentType(VisualEffectComp.class)) {
            VisualEffectComp veComp = (VisualEffectComp)wc.getComponent(entity, VisualEffectComp.class);

            updateVisualEffectComp(entity, veComp);
        }

        //actually update effects
        effectsRunning.forEach(ve -> {

            updateVisualEffect(ve);
        });

        removeEffects();
    }

    @Override
    public void terminate() {

    }

    private void updateVisualEffectComp(int entity, VisualEffectComp  veComp) {
        while(veComp.hasEffectsToStart()) {
            VisualEffect effect = veComp.popVisualEffect();

            if (effectsRunning.contains( effect )) { //CANGED FROM: veComp.popVisualEffect()
                effectsRunning.remove(effect);
            }
            effectsRunning.add(effect);
        }
    }


    private void updateVisualEffect(VisualEffect effect) {
        effect.activeParticleStream().forEach(p -> {

            updateParticle(p);

        });

//        System.out.println("Visual effect lifetime= "+effect.getLifetime());

        effect.decrementLifetime();
        if (effect.getLifetime() <= 0) {
            effect.endEffect();
            removeEffects.add(effect);
        }
    }

    private void updateParticle(Particle p) {
        p.addPos( p.getVelocity() );
//        System.out.println("Updating particle, pos="+p.getPos());

        p.decrementLifetime();
        if (p.getLifetime() <= 0) {
            p.deactivate();

        }
    }



    private void removeEffects() {
        while( !removeEffects.isEmpty()) {

            effectsRunning.remove(removeEffects.poll());
        }
    }

    public Stream<VisualEffect> visualEffectStream() {
        return effectsRunning.stream();
    }
    public Stream<VisualEffect> activeVisualEffectStream() {
        return effectsRunning.stream().filter(ve -> ve.isActive());
    }

//    public void forEachActiveParticle(Consumer<? super Particle> predicate) {
//        activeVisualEffectStream().forEach(ve -> ve.activeParticleStream().forEach(predicate));
//    }
}
