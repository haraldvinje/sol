package engine.visualEffect;

import engine.graphics.ColoredMesh;
import engine.graphics.ColoredMeshUtils;
import utils.maths.M;
import utils.maths.Vec2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by eirik on 06.07.2017.
 */
public class VisualEffect {

    private boolean active = false;

    private int attrParticleCount;

    private float attrLifetime;
    private float attrEffectAngle;

//    private float attrRadiusMin, attrRadiusMax;
    private float attrSpeedMin, attrSpeedMax;
    private float attrAngleCenter, attrAngleSpread;


    private List<Particle> particles = new ArrayList<>();


    private float lifetime;


    public VisualEffect(int particleCount, float lifetime, float speedMin, float speedMax, float angleCenter, float angleSpread) {
        this.attrLifetime = lifetime;
        this.attrParticleCount = particleCount;

        this.attrSpeedMin = speedMin;
        this.attrSpeedMax = speedMax;

        this.attrAngleCenter = angleCenter;
        this.attrAngleSpread = angleSpread;

        createParticles(particleCount);

        resetThis();
    }

    private void createParticles(int count) {
        for (int i = 0; i < count; i++) {
            float[] color = {1, 0, 0};
            ColoredMesh mesh = ColoredMeshUtils.createCircleSinglecolor(6, 8, color);

            Particle p = new Particle(mesh);
            particles.add(p);
        }
    }

    private void startParticles(Vec2 pos) {
        for (Particle p : particles) {
            p.activate();

            p.setLifetime(attrLifetime);

            p.setPos(pos);
            Vec2 vel = Vec2.newLenDir((attrSpeedMax-attrSpeedMin)* M.random()+attrSpeedMin,
                    attrAngleCenter + attrAngleSpread*(M.random() - 0.5f) );

            p.setVelocity(vel);
        }
    }

    public void startEffect(Vec2 pos) {
        resetThis();
        startParticles(pos);
        active = true;
        System.out.println("Starting effect, particle count="+particles.size());
    }



    public Stream<Particle> allParticleStream() {
        return particles.stream();
    }
    public Stream<Particle> activeParticleStream() {
        return particles.stream().filter(p -> p.isActive());
    }

    public boolean isActive() {
        return active;
    }
    public void decrementLifetime() {
        lifetime--;
    }
    public float getLifetime() {
        return lifetime;
    }
    public void endEffect() {
        activeParticleStream().forEach(p -> p.deactivate());
        active = false;
    }


//    private void reset() {
////
//        resetThis();
//    }

    private void resetThis() {
        lifetime = attrLifetime;
    }
}
