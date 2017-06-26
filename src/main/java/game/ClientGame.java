package game;

import engine.PositionComp;
import engine.RotationComp;
import engine.UserInput;
import engine.WorldContainer;
import engine.character.*;
import engine.combat.DamageResolutionSys;
import engine.combat.DamageableComp;
import engine.combat.DamagerComp;
import engine.graphics.*;
import engine.network.client.ClientNetworkSys;
import engine.physics.*;
import engine.window.Window;

/**
 * Created by eirik on 22.06.2017.
 */
public class ClientGame {

    private static final float FRAME_INTERVAL = 1.0f/60.0f;

    public static final float WINDOW_WIDTH = 1600f, WINDOW_HEIGHT = 900f;


    private Window window;
    private UserInput userInput;


    private long lastTime;

    private WorldContainer wc;



    private int[] players;
    private int sandbag;
    private int hole;


    private ClientNetworkSys clientSys;


    public void init() {
        window = new Window(WINDOW_WIDTH, WINDOW_HEIGHT, "Client   SIIII");
        userInput = new UserInput(window);

        wc = new WorldContainer();

        System.out.println("HEELLLLLOOOOO");


        //set program state
        GameUtils.ON_SERVER = false;

        GameUtils.assignComponentTypes(wc);

        GameUtils.assignSystems(wc, window, userInput);


        GameUtils.createInitialEntities(wc);

    }


    /**
     * blocking while the game runs
     */
    public void start() {
        lastTime = System.nanoTime();

        float timeSinceUpdate = 0;

        while (true) {
            timeSinceUpdate += timePassed();
            //System.out.println("Time since update: "+timeSinceUpdate);

            if (timeSinceUpdate >= FRAME_INTERVAL) {
                timeSinceUpdate -= FRAME_INTERVAL;

                update();
            }


            if (window.shouldClosed() || userInput.isKeyboardPressed(UserInput.KEY_ESCAPE))
                break;
        }

        close();

    }

    private int createPlayer(WorldContainer wc, String imagePath) {
        int player = wc.createEntity();
        float radius = 32f;
        float xoffset = 16f;
        wc.addComponent(player, new CharacterComp());
//        wc.addComponent(player, new CharacterInputComp());
//        wc.addComponent(player, new UserCharacterInputComp());

        wc.addComponent(player, new PositionComp(WINDOW_WIDTH/2f, WINDOW_HEIGHT/2f));
        wc.addComponent(player, new RotationComp());

//        wc.addComponent(player, new PhysicsComp(80, 2.5f, 0.3f, PhysicsUtil.FRICTION_MODEL_VICIOUS));
//        wc.addComponent(player, new CollisionComp(new Circle(radius)));

        wc.addComponent(player, new TexturedMeshComp(TexturedMeshUtils.createRectangle(imagePath, 4*radius*2, 2*radius*2)));
        wc.addComponent(player, new MeshCenterComp(radius*2+xoffset, radius*2));
//        wc.addComponent(player, new AffectedByHoleComp());

        //wc.addComponent(player, new DamageableComp());

        //wc.addComponent(player, new DamagerComp());

        return player;
    }
    private int createSandbag(WorldContainer wc) {
        float radius = 32f;
        int sandbag = wc.createEntity();
        wc.addComponent(sandbag, new PositionComp(WINDOW_WIDTH/2f, WINDOW_HEIGHT/2f) );
        wc.addComponent(sandbag, new TexturedMeshComp(TexturedMeshUtils.createRectangle("sandbag.png", radius*2, radius*2)));
        wc.addComponent(sandbag, new MeshCenterComp(radius, radius));

//        wc.addComponent(sandbag, new PhysicsComp(80, 2.5f, 0.3f, PhysicsUtil.FRICTION_MODEL_VICIOUS));
//        wc.addComponent(sandbag, new CollisionComp(new Rectangle(radius*2, radius*2)));
//
//        wc.addComponent(sandbag, new DamageableComp());
//        wc.addComponent(sandbag, new AffectedByHoleComp());

        //wc.addComponent(sandbag, new CharacterComp());

        return sandbag;
    }

    private int createHole(WorldContainer wc) {
        float radius = 32.0f;
        int hole = wc.createEntity();
        wc.addComponent(hole, new PositionComp(1000, 300));
        wc.addComponent(hole, new TexturedMeshComp(TexturedMeshUtils.createRectangle("sandbag.png", radius * 2, radius * 2)));
        wc.addComponent(hole, new MeshCenterComp(radius, radius));

        wc.addComponent(hole, new PhysicsComp(500f, 10.0f));
        wc.addComponent(hole, new CollisionComp(new Rectangle(radius, radius)));
        wc.addComponent(hole, new HoleComp());


        return hole;
    }

    private int createBackground(WorldContainer wc) {
        int bg = wc.createEntity();
        wc.addComponent(bg, new PositionComp(0, 0));
        wc.addComponent(bg, new TexturedMeshComp(TexturedMeshUtils.createRectangle("background_difuse.png", 1600, 900)));

        return bg;
    }

    private int createWall(WorldContainer wc, float x, float y, float width, float height) {
        int w = wc.createEntity();
        wc.addComponent(w, new PositionComp(x, y));
        wc.addComponent(w, new PhysicsComp(0, 1, 1));
        wc.addComponent(w, new CollisionComp(new Rectangle(width, height)));

        wc.addComponent(w, new ColoredMeshComp(ColoredMeshUtils.createRectangle(width, height)));
        wc.addComponent(w, new MeshCenterComp(width/2, height/2)); //physical rectangle is defined with position being the center, while the graphical square is defined in the upper left corner

        return w;
    }


    public void update() {

/*        System.out.println(wc.getPositionComps());
        System.out.println(wc.getVelocityComps());
        System.out.println(wc.getCollisionComps());
        wc.updateSystems();*/

        //System.out.println( ((PositionComp)wc.getComponent(player, WorldContainer.COMPMASK_POSITION)).getX() );

        window.pollEvents();


        //collision system
        //cds.update();

        wc.updateSystems();
    }

    private void close() {
        clientSys.close();

        window.close();
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
