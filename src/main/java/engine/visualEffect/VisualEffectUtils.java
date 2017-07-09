package engine.visualEffect;

import engine.graphics.ColoredMeshUtils;
import utils.maths.M;
import utils.maths.Vec2;

/**
 * Created by eirik on 06.07.2017.
 */
public class VisualEffectUtils {

    public static VisualEffect createOnHitEffect() {

        int particleCount = 300;//16;
        int lifetime = 15;

        float speedMax = 8;
        float speedMin = -4;
        float angleCenter = M.PI;
        float angleSpread = 2*M.PI;

        return new VisualEffect(particleCount, lifetime, speedMin, speedMax, angleCenter, angleSpread);
    }
}
