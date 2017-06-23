package game;


import engine.*;

import engine.character.*;
import engine.combat.DamageResolutionSys;
import engine.combat.DamageableComp;
import engine.combat.DamagerComp;
import engine.combat.abilities.Ability;
import engine.combat.abilities.AbilityComp;
import engine.combat.abilities.AbilitySys;
import engine.combat.abilities.MeleeAbility;
import engine.graphics.*;
import engine.physics.*;
import engine.window.Window;

/**
 * Created by eirik on 13.06.2017.
 */
public class Game {


    private static final float FRAME_INTERVAL = 1.0f/60.0f;

    public static final float WINDOW_WIDTH = 1600f, WINDOW_HEIGHT = 900f;


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

        System.out.println("HEELLLLLOOOOO");

      
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
        wc.assignComponentType(DamageableComp.class);
        wc.assignComponentType(DamagerComp.class);
        wc.assignComponentType(AffectedByHoleComp.class);
        wc.assignComponentType(AbilityComp.class);
      

        //add systems
        wc.addSystem(new CharacterSys());
        wc.addSystem(new UserCharacterInputSys(userInput));

        wc.addSystem(new CollisionDetectionSys());
        wc.addSystem(new HoleResolutionSys());
        wc.addSystem(new AbilitySys());
        wc.addSystem(new DamageResolutionSys());
        wc.addSystem(new CollisionResolutionSys());

        wc.addSystem(new PhysicsSys());
        wc.addSystem(new RenderSys(window));




        player = createPlayer(wc);
        sandbag = createSandbag(wc);

        float wallThickness = 64f;
        createWall(wc, wallThickness/2, WINDOW_HEIGHT/2, wallThickness, WINDOW_HEIGHT);
        createWall(wc, WINDOW_WIDTH-wallThickness/2, WINDOW_HEIGHT/2, wallThickness, WINDOW_HEIGHT);

        createWall(wc, WINDOW_WIDTH/2, wallThickness/2, WINDOW_WIDTH-wallThickness*2, wallThickness);
        createWall(wc, WINDOW_WIDTH/2, WINDOW_HEIGHT-wallThickness/2, WINDOW_WIDTH-wallThickness*2, wallThickness);


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
            //System.out.println("Time since update: "+timeSinceUpdate);

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
        float xoffset = 16f;
        wc.addComponent(player, new CharacterComp());
        wc.addComponent(player, new CharacterInputComp());
        wc.addComponent(player, new UserCharacterInputComp());

        wc.addComponent(player, new PositionComp(WINDOW_WIDTH/2f, WINDOW_HEIGHT/2f));
        wc.addComponent(player, new RotationComp());

        wc.addComponent(player, new PhysicsComp(80, 2.5f, 0.3f, PhysicsUtil.FRICTION_MODEL_VICIOUS));
        wc.addComponent(player, new CollisionComp(new Circle(radius)));

        wc.addComponent(player, new TexturedMeshComp(TexturedMeshUtils.createRectangle("sol_frank.png", 4*radius*2, 2*radius*2)));
        wc.addComponent(player, new MeshCenterComp(radius*2+xoffset, radius*2));
        wc.addComponent(player, new AffectedByHoleComp());

        wc.addComponent(player, new DamageableComp());

        //Creating an ability for the player. Starting off with melee attacks
        AbilityComp ability = new AbilityComp();

        float hitboxRadius = 16f;
        Circle hitbox = new Circle(hitboxRadius);
        MeleeAbility meleeAbility = new MeleeAbility(wc, hitbox, 82.0f, 5, 1, 5, 5);
        ability.addMeleeAbility(meleeAbility);

        float hboxRadius = 32f;
        Circle hbox = new Circle(hboxRadius);
        MeleeAbility melAb = new MeleeAbility(wc, hbox, - 102f, 5,1,5,5);
        ability.addMeleeAbility(melAb);

        wc.addComponent(player, ability);



        return player;
    }
    private int createSandbag(WorldContainer wc) {
        float radius = 32f;
        int sandbag = wc.createEntity();
        wc.addComponent(sandbag, new PositionComp(500, 300) );
        wc.addComponent(sandbag, new TexturedMeshComp(TexturedMeshUtils.createRectangle("sandbag.png", radius*2, radius*2)));
        wc.addComponent(sandbag, new MeshCenterComp(radius, radius));

        wc.addComponent(sandbag, new PhysicsComp(80, 2.5f, 0.3f, PhysicsUtil.FRICTION_MODEL_VICIOUS));
        wc.addComponent(sandbag, new CollisionComp(new Rectangle(radius*2, radius*2)));

        wc.addComponent(sandbag, new DamageableComp());

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
