package game;


import engine.*;

import engine.character.*;
import engine.graphics.*;
import engine.physics.Circle;
import engine.physics.CollisionComp;
import engine.physics.VelocityComp;
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
        wc.assignComponentType(VelocityComp.class);
        wc.assignComponentType(CharacterComp.class);
        wc.assignComponentType(CharacterInputComp.class);
        wc.assignComponentType(UserCharacterInputComp.class);
        wc.assignComponentType(RotationComp.class);
        wc.assignComponentType(MeshCenterComp.class);

        //add systems
        wc.addSystem(new RenderSys(window));
        wc.addSystem(new CharacterSys());
        wc.addSystem(new UserCharacterInputSys(userInput));



        player = createPlayer(wc);
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
        wc.addComponent(player, new CharacterComp());
        wc.addComponent(player, new CharacterInputComp());
        wc.addComponent(player, new UserCharacterInputComp());

        wc.addComponent(player, new PositionComp(0, 0));
        wc.addComponent(player, new RotationComp());
        //wc.addComponent(player, new ColoredMeshComp( ColoredMeshUtils.createRectangle(32, 32)));
        wc.addComponent(player, new TexturedMeshComp(TexturedMeshUtils.createRectangle("frank_original_swg.png", 128, 64)));
        wc.addComponent(player, new MeshCenterComp(32, 32));

        return player;
    }
    private int createSandbag(WorldContainer wc) {
        int sandbag = wc.createEntity();
        wc.addComponent(sandbag, new PositionComp(102, 102) );
        wc.addComponent(sandbag, new CollisionComp(new Circle( 5)) );
        return sandbag;
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
