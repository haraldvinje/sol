package game;

import engine.Circle;
import engine.WorldContainer;

/**
 * Created by eirik on 13.06.2017.
 */
public class Game {

    private static final float FRAME_INTERVAL = 1.0f/60.0f;

    private WorldContainer wc;


    private long lastTime;


    private int player;
    private int sandbag;


    public void init() {
        wc = new WorldContainer();


        this.player = wc.createEntity();

        wc.createPositionComp(player, 100, 100);
        wc.createVelocityComp(player, 0,0);
        Circle c1 = new Circle();
        c1.setRadius(10);
        wc.createCollisionComp(player, c1);

        this.sandbag = wc.createEntity();

        wc.createPositionComp(sandbag, 109, 109);
        wc.createVelocityComp(sandbag, 0,0);
        Circle c2 = new Circle();
        c2.setRadius(10);
        wc.createCollisionComp(sandbag, c2);

        wc.init();

        System.out.println(5 & 4);

    }


    /**
     * blocking while the game runs
     */
    public void start() {
        lastTime = System.nanoTime();

        float timeSinceUpdate = 0;

        while (true) {
            timeSinceUpdate += timePassed();

            if (timeSinceUpdate >= FRAME_INTERVAL) {
                timeSinceUpdate -= FRAME_INTERVAL;

                update();
            }

        }

    }
    public void update() {
/*        System.out.println(wc.getPositionComps());
        System.out.println(wc.getVelocityComps());
        System.out.println(wc.getCollisionComps());
        wc.updateSystems();*/
    }


    /**
     * time passed since last call to this method
     * @return
     */
    private float timePassed() {
        long newTime = System.nanoTime();
        int deltaTime = (int)(newTime - lastTime);
        float deltaTimeF = (float) deltaTime;

        lastTime = newTime;

        return deltaTimeF/1000000000;
    }
}
