package game;


import engine.*;

import engine.character.*;
import engine.graphics.*;
import engine.physics.*;
import engine.window.Window;

/**
 * Created by eirik on 13.06.2017.
 */
public class Game {

    private static final float FRAME_INTERVAL = 1.0f/60.0f;



    private Window window;
    private UserInput userInput;


    private ColoredMesh vao;

    private long lastTime;

    private WorldContainer wc;

    //private CollisionDetectionSys cds;


    private int player;
    private int sandbag;
    private int hole;




    public void init() {
        window = new Window(1600, 900, "SIIII");
        userInput = new UserInput(window);

        wc = new WorldContainer();

        //cds = new CollisionDetectionSys(wc);


      
        //assign component types
        wc.assignComponentType(PositionComp.class);
        wc.assignComponentType(ColoredMeshComp.class);
        wc.assignComponentType(TexturedMeshComp.class);
        wc.assignComponentType(CollisionComp.class);
        wc.assignComponentType(PhysicsComp.class);
        wc.assignComponentType(CharacterComp.class);
        wc.assignComponentType(CharacterInputComp.class);
        wc.assignComponentType(UserCharacterInputComp.class);
        wc.assignComponentType(RotationComp.class);
        wc.assignComponentType(MeshCenterComp.class);
        wc.assignComponentType(HoleComp.class);

        //add systems
        wc.addSystem(new RenderSys(window));
        wc.addSystem(new CharacterSys());
        wc.addSystem(new UserCharacterInputSys(userInput));

        wc.addSystem(new CollisionDetectionSys());
        wc.addSystem(new HoleResolutionSys());

        wc.addSystem(new CollisionResolutionSys());
        wc.addSystem(new PhysicsSys());




        player = createPlayer(wc);
        sandbag = createSandbag(wc);
        hole = createHole(wc);
        createBackground(wc);

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


            if (window.shouldClosed() || userInput.isKeyboardPressed(UserInput.KEY_ESCAPE))
                break;
        }

        window.close();

    }

    private int createPlayer(WorldContainer wc) {
        int player = wc.createEntity();
        float radius = 32f;
        wc.addComponent(player, new CharacterComp());
        wc.addComponent(player, new CharacterInputComp());
        wc.addComponent(player, new UserCharacterInputComp());

        wc.addComponent(player, new PositionComp(0, 0));
        wc.addComponent(player, new RotationComp());

        wc.addComponent(player, new PhysicsComp(80, 2.5f, 0.3f, PhysicsUtil.FRICTION_MODEL_VICIOUS));
        wc.addComponent(player, new CollisionComp(new Circle(radius)));

        wc.addComponent(player, new TexturedMeshComp(TexturedMeshUtils.createRectangle("frank_original_swg.png", 4*radius, 2*radius)));
        wc.addComponent(player, new MeshCenterComp(32, 32));

        return player;
    }
    private int createSandbag(WorldContainer wc) {
        float radius = 32f*4;
        int sandbag = wc.createEntity();
        wc.addComponent(sandbag, new PositionComp(500, 300) );
        wc.addComponent(sandbag, new TexturedMeshComp(TexturedMeshUtils.createRectangle("sandbag.png", radius*2, radius*2)));
        wc.addComponent(sandbag, new MeshCenterComp(radius, radius));

        wc.addComponent(sandbag, new PhysicsComp(500f, 10.0f));
        wc.addComponent(sandbag, new CollisionComp(new Rectangle(radius*2, radius*2)));

        return sandbag;
    }


    private int createHole(WorldContainer wc) {
        float radius = 32.0f;
        int hole = wc.createEntity();
        wc.addComponent(hole, new PositionComp(1000, 300));
        wc.addComponent(hole, new TexturedMeshComp(TexturedMeshUtils.createRectangle("sandbag.png", radius * 2, radius * 2)));
        wc.addComponent(hole, new MeshCenterComp(radius, radius));

        wc.addComponent(hole, new PhysicsComp(500f, 10.0f));
        wc.addComponent(hole, new CollisionComp(new Circle(radius)));
        wc.addComponent(hole, new HoleComp());


        return hole;
    }

    private void createBackground(WorldContainer wc) {
        int bg = wc.createEntity();
        wc.addComponent(bg, new PositionComp(0, 0));
        wc.addComponent(bg, new TexturedMeshComp(TexturedMeshUtils.createRectangle("background_difuse.png", 1600, 900)));

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
