package Game;

import GameController.BushGhost;
import GameController.SaGhost;
import GameController.WormGhost;
import GameModel.Bush;
import GameModel.Explosion;
import GameModel.Fruit;
import GameModel.Rock;
import GameModel.Ruby;
import GameModel.Sa;
import GameModel.Win;
import GameModel.Worm;
import Model.Account;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.TouchTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Thong
 */
public class Scene extends AbstractAppState implements GameConstant, ActionListener {

    private boolean isWon = false;
    private boolean isBombReady = true;
    //Game Apps
    private Game app;
    private Node rootNode = new Node("root");
    private AssetManager assetManager;
    private InputManager inputManager;
    private Camera cam;
    //init the acount of player
    private Account account;
    //init the physic world and the main camera
    private BulletAppState bullet;
    private ChaseCamera chaseCam;
    //init the character
    private Sa sa;
    private AudioNode saDieSound;
    //init fruit
    private ArrayList<Fruit> fruits = new ArrayList<Fruit>();
    private float fruitSize = .8f;
    private Spatial fruitSpatial;
    private Material fruitMaterial;
    private ArrayList<Explosion> explodes;
    //Sound
    private AudioNode explodeSound;
    //Ruby
    Spatial ruby;
    Material rubyMat;
    AudioNode rubySound;
    ArrayList<Ruby> rubies = new ArrayList<Ruby>();
    //Gate
    Spatial gate;
    Material gateMat;
    //Bush
    private byte bushNum = 1;
    private ArrayList<Bush> bushes;
    //Worm
    private boolean isBoss = false;
    private byte wormNum = 1;
    private AudioNode wormDieSound;
    private ArrayList<Worm> worms;
    //Floor
    Geometry floor_geo;
    TextureKey key3;

    public Scene(AppStateManager stateManager, SimpleApplication app) {
        this.app = (Game) app;
        this.assetManager = this.app.getAssetManager();
        this.inputManager = this.app.getInputManager();
        this.cam = this.app.getCamera();

        //build the physic world
        bullet = new BulletAppState();
        bullet.setThreadingType(BulletAppState.ThreadingType.PARALLEL);

        stateManager.attach(bullet);

//        bullet.getPhysicsSpace().enableDebug(assetManager);

        init();

        setUpLight();

        setUpRock();

        setUpMainChar();

        setUpCammera();

        setUpSound();
    }

    private void init() {
        rootNode.attachChild(assetManager.loadModel("Models/sky.j3o"));
        explodes = new ArrayList<Explosion>();
        ruby = assetManager.loadModel("Models/ruby/ruby.j3o");
        rubyMat = assetManager.loadMaterial("Materials/ruby.j3m");

        gate = assetManager.loadModel("Models/win/win.j3o");
        gateMat = assetManager.loadMaterial("Materials/win.j3m");
    }

    public void init(byte lv, Account account, byte wormNum, byte bushNum) {
        this.account = account;
        this.wormNum = wormNum;
        this.bushNum = bushNum;

        registerSaControl();
        isBoss = false;
        float speedRatio = 1f;
        byte smartLv = 10;
        switch (lv) {
            case 1:
            case 2:
            case 3:
                key3 = new TextureKey("Textures/ground.png");
                break;
            case 4:
            case 5:
            case 6:
                speedRatio = 1.3f;
                smartLv = 8;
                key3 = new TextureKey("Textures/ground1.jpg");
                break;
            case 7:
            case 8:
            case 9:
                speedRatio = 1.6f;
                smartLv = 6;
                key3 = new TextureKey("Textures/ground2.jpg");
                break;
            case 10:
                isBoss = true;
                speedRatio = 2.0f;
                smartLv = 4;
                key3 = new TextureKey("Textures/ground2.jpg");
                break;
        }

        setUpMap();

        if (account.getFruit() == account.STRAWBERRY) {
            fruitSize = 1.2f;
            sa.speedBoosted();
            fruitSpatial = assetManager.loadModel(STRAWBERRY_OBJECT);
            fruitMaterial = assetManager.loadMaterial(SRTAWBERRY_MAT);
        } else {
            fruitSpatial = assetManager.loadModel(APPLE_OBJECT);
            fruitMaterial = assetManager.loadMaterial(APPLE_MAT);
        }

        rootNode.attachChild(sa);
        bullet.getPhysicsSpace().add(sa);
        sa.reset();

        sa.setIsShield(account.isShield());
        sa.getControl(SaGhost.class).setPhysicsLocation(new Vector3f(PLACE_HOLDER * (MAP_WIDTH - 3),
                PLACE_HOLDER * 2, PLACE_HOLDER * -(MAP_HEIGHT - 3)));

        setUpBushWorm(speedRatio, smartLv);
    }

    public Node getRootNode() {
        return rootNode;
    }

    //<editor-fold defaultstate="collapsed" desc="setUpBushWorm">
    private void setUpBushWorm(float speedRatio, byte smartLv) {
        worms = new ArrayList<Worm>(wormNum);
        bushes = new ArrayList<Bush>(bushNum);
        Spatial wormSpatial = assetManager.loadModel("Models/worm1/worm.j3o");
        Spatial bushSpatial = assetManager.loadModel("Models/bush/bush.j3o");

        Material wormMaterial = assetManager.loadMaterial("Materials/worm1.j3m");
        Material bushMaterial = assetManager.loadMaterial("Materials/bush.j3m");

        Bush bush = new Bush(bushSpatial);
        bush.setSkin(bushMaterial);
        bush.setScene(this);
        bushes.add(bush);
        rootNode.attachChild(bush);
        bullet.getPhysicsSpace().add(bush);
        for (int i = 1; i < bushNum; i++) {
            Bush temp = new Bush(bushSpatial.clone());
            temp.setSkin(bushMaterial);
            temp.setScene(this);
            bushes.add(temp);
            rootNode.attachChild(temp);
            bullet.getPhysicsSpace().add(temp);
        }

        Worm worm = new Worm(wormSpatial, speedRatio, smartLv);
        worm.setSkin(wormMaterial);
        worm.setScene(this);
        worms.add(worm);
        rootNode.attachChild(worm);
        bullet.getPhysicsSpace().add(worm);
        for (int i = 1; i < wormNum; i++) {
            Worm temp = new Worm(wormSpatial.clone(), speedRatio, smartLv);
            temp.setSkin(wormMaterial);
            temp.setScene(this);
            worms.add(temp);
            rootNode.attachChild(temp);
            bullet.getPhysicsSpace().add(temp);
        }

        MapGenerator.generate(worms, bushes);
    }

    public void bushDie(Bush bush) {
        Vector3f position = bush.getControl(RigidBodyControl.class).getPhysicsLocation();
        byte x = (byte) Math.round((-position.x / PLACE_HOLDER + MAP_WIDTH - 1) / 2);
        byte y = (byte) Math.round((position.z / PLACE_HOLDER + MAP_HEIGHT - 1) / 2);

        POSITIONS[x][y][0] = EMPTY;
        bush.detachAllChildren();
        bullet.getPhysicsSpace().remove(bush.getControl(BushGhost.class));
        bullet.getPhysicsSpace().remove(bush);
        bush.removeFromParent();

        bushes.remove(bush);

        addRuby(x, y);
    }

    public void wormDie(Worm worm) {
        Vector3f position = worm.getControl(CharacterControl.class).getPhysicsLocation();
        byte x = (byte) Math.round((-position.x / PLACE_HOLDER + MAP_WIDTH - 1) / 2);
        byte y = (byte) Math.round((position.z / PLACE_HOLDER + MAP_HEIGHT - 1) / 2);



        POSITIONS[x][y][0] = EMPTY;
        worm.detachAllChildren();
        bullet.getPhysicsSpace().remove(worm.getControl(WormGhost.class));
        bullet.getPhysicsSpace().remove(worm);
        worm.removeFromParent();

        worms.remove(worm);

        wormDieSound.playInstance();

        addRuby(x, y);
    }

    private void addRuby(byte x, byte y) {
        Ruby rub = new Ruby(ruby.clone());
        rub.setMaterial(rubyMat);
        POSITIONS[x][y][0] = RUBY;
        RUBY_POS[x][y][0] = rub;
        rootNode.attachChild(rub);
        bullet.getPhysicsSpace().add(rub);
        rub.render(x, y);
        rubies.add(rub);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="setUpRock">
    private void setUpRock() {
        Spatial rockSpatial = assetManager.loadModel("Models/rock/rock.j3o");
        Material rockMaterial = assetManager.loadMaterial("Materials/rock.j3m");
        //<editor-fold defaultstate="collapsed" desc="set up boundary rocks">
        // horizontal rocks (rocks along the X axis)
        for (int i = 0; i < MAP_WIDTH; i++) {
            setUpRock(rockSpatial, rockMaterial, i, MAP_HEIGHT - 1);
            setUpRock(rockSpatial, rockMaterial, i, 0);
        }
        // vertical rocks (rocks along the Z axis)
        for (int i = 1; i < MAP_HEIGHT - 1; i++) {
            setUpRock(rockSpatial, rockMaterial, 0, i);
            setUpRock(rockSpatial, rockMaterial, MAP_WIDTH - 1, i);
        }
        //</editor-fold>

        // set up in-game rocks
        for (int i = 2; i < MAP_WIDTH - 2; i += 2) {
            for (int j = 2; j < MAP_HEIGHT - 2; j += 2) {
                setUpRock(rockSpatial, rockMaterial, i, j);
            }
        }
    }

    //convenient method
    private void setUpRock(Spatial rockSpatial, Material material, int x, int y) {
        Rock rock = new Rock(rockSpatial.clone());
        rock.setSkin(material);
        rootNode.attachChild(rock);
        rock.render(x, y);
        bullet.getPhysicsSpace().add(rock);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="setUpLight">
    private void setUpLight() {
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(.5f));
        rootNode.addLight(al);

        DirectionalLight sun = new DirectionalLight();
        sun.setColor(ColorRGBA.White.mult(.8f));
        sun.setDirection(new Vector3f(0f, -.8f, 1f).normalizeLocal());
        rootNode.addLight(sun);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="setUpCamera">
    private void setUpCammera() {
        // Disable the default first-person cam!
        app.getFlyByCamera().setEnabled(false);
        cam.setRotation(new Quaternion(new float[]{0, (float) Math.PI / 2, 0}));
        // Enable a chase cam
        chaseCam = new ChaseCamera(cam, sa, inputManager);
        chaseCam.setDefaultDistance(70f);
        chaseCam.setMaxDistance(150f);
        chaseCam.setMinDistance(50f);
        chaseCam.setDefaultHorizontalRotation(-FastMath.PI / 2);
        chaseCam.setDefaultVerticalRotation(FastMath.PI / 4);
        chaseCam.setInvertVerticalAxis(true);
        chaseCam.setTrailingEnabled(false);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="setUpMap">
    private void setUpMap() {
        Material floor_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");


        key3.setGenerateMips(true);

        Texture tex3 = assetManager.loadTexture(key3);
        tex3.setWrap(WrapMode.Repeat);
        floor_mat.setTexture("ColorMap", tex3);

        Box floor = new Box(MAP_WIDTH * PLACE_HOLDER, 1f, MAP_HEIGHT * PLACE_HOLDER);
        floor.scaleTextureCoordinates(new Vector2f(MAP_HEIGHT, MAP_WIDTH));

        floor_geo = new Geometry("Floor", floor);
        floor_geo.setMaterial(floor_mat);
        rootNode.attachChild(floor_geo);
        /* Make the floor physical with mass 0.0f! */
        RigidBodyControl floor_phy = new RigidBodyControl(0.0f);
        floor_geo.addControl(floor_phy);
        floor_phy.setPhysicsLocation(new Vector3f(0, -1f, 0));
        floor_geo.getControl(RigidBodyControl.class).setFriction(.5f);
        bullet.getPhysicsSpace().add(floor_geo);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Main Character Sa">
    private void setUpMainChar() {
        //create the main character
        sa = new Sa(assetManager.loadModel("Models/sa/sa.j3o"));
        sa.setSkin(assetManager.loadMaterial("Materials/sa.j3m"));
        sa.setScene(this);

        rootNode.attachChild(sa);

        sa.render(1, 1);
        SA_POS[0][0] = sa.getControl(SaGhost.class).getPhysicsLocation();
        SA_POS[0][1] = sa;
        bullet.getPhysicsSpace().add(sa);

    }

    public void saDie(Sa sa) {

        bullet.getPhysicsSpace().remove(sa);
        sa.removeFromParent();
        account.reset();
        if (!isMute[0]) {
            saDieSound.playInstance();
        }
        app.end();

    }

    private void registerSaControl() {

        inputManager.addMapping("Touch", new TouchTrigger(0));
        inputManager.addListener(sa, "Touch");

        inputManager.setSimulateMouse(false);
        inputManager.setSimulateKeyboard(false);

        inputManager.addMapping("CharLeft", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("CharRight", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("CharUp", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("CharDown", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("CharBang", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(sa, "CharLeft");
        inputManager.addListener(sa, "CharRight");
        inputManager.addListener(sa, "CharUp");
        inputManager.addListener(sa, "CharDown");
        inputManager.addListener(this, "CharBang");
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("CharBang") && isBombReady) {
            plantBomb();
        }
    }

    private static boolean isAvailable(byte x, byte y) {
        if (POSITIONS[x][y][0] == EMPTY || POSITIONS[x][y][0] == WORM) {
            return true;
        } else {
            return false;
        }
    }

    public void plantBomb() {
        if (fruits.size() < account.getCapacity()) {
            final Vector3f position = sa.getControl(CharacterControl.class).getPhysicsLocation();
            byte x = (byte) Math.round((-position.x / PLACE_HOLDER + MAP_WIDTH - 1) / 2);
            byte y = (byte) Math.round((position.z / PLACE_HOLDER + MAP_HEIGHT - 1) / 2);

            if (isAvailable(x, y)) {
                Fruit fruit = new Fruit(fruitSpatial.clone());
                fruit.setSkin(fruitMaterial);

                rootNode.attachChild(fruit);
                bullet.getPhysicsSpace().add(fruit);

                fruit.render(x, y, fruitSize);
                POSITIONS[x][y][0] = FRUIT;
                BOMB_POS[x][y][0] = fruit;
                fruit.setScene(this);
                fruits.add(fruit);
                isBombReady = false;
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        isBombReady = true;
                    }
                }, 300);
            }
        }
    }

    public void bang(Fruit fruit) {
        Vector3f position = fruit.getControl(CharacterControl.class).getPhysicsLocation();
        byte x = (byte) Math.round((-position.x / PLACE_HOLDER + MAP_WIDTH - 1) / 2);
        byte y = (byte) Math.round((position.z / PLACE_HOLDER + MAP_HEIGHT - 1) / 2);

        if (!isMute[0]) {
            explodeSound.playInstance();
        }
//        if (fruit.getControl(RigidBodyControl.class) != null) {
//            bullet.getPhysicsSpace().remove(fruit.getControl(RigidBodyControl.class));
//        }
        fruits.remove(fruit);
        POSITIONS[x][y][0] = EMPTY;
        fruit.detachAllChildren();
        bullet.getPhysicsSpace().remove(fruit);
        fruit.removeFromParent();
        explode(position.clone());
        for (int i = 1; i <= account.getPower(); i++) {
            if (x + i < MAP_WIDTH) {
                if (POSITIONS[x + i][y][0] == ROCK) {
                    break;
                }

                boolean isBush = false;

                if (POSITIONS[x + i][y][0] == BUSH) {
                    isBush = true;
                    Bush bush = (Bush) BUSH_POS[x + i][y][0];
                    bush.die();
                }

                Vector3f temp = position.clone();
                temp.x = temp.x - (PLACE_HOLDER * 2) * i;
                explode(temp);

                if (isBush) {
                    break;
                }

            }
        }

        for (int i = 1; i <= account.getPower(); i++) {
            if (x - i >= 0) {
                if (POSITIONS[x - i][y][0] == ROCK) {
                    break;
                }

                boolean isBush = false;

                if (POSITIONS[x - i][y][0] == BUSH) {
                    isBush = true;
                    Bush bush = (Bush) BUSH_POS[x - i][y][0];
                    bush.die();
                }

                Vector3f temp = position.clone();
                temp.x = temp.x + (PLACE_HOLDER * 2) * i;
                explode(temp);

                if (isBush) {
                    break;
                }

            }
        }

        for (int i = 1; i <= account.getPower(); i++) {
            if (y + 1 < MAP_HEIGHT) {
                if (POSITIONS[x][y + i][0] == ROCK) {
                    break;
                }

                boolean isBush = false;

                if (POSITIONS[x][y + i][0] == BUSH) {
                    isBush = true;
                    Bush bush = (Bush) BUSH_POS[x][y + i][0];
                    bush.die();
                }

                Vector3f temp = position.clone();
                temp.z = temp.z + (PLACE_HOLDER * 2) * i;
                explode(temp);

                if (isBush) {
                    break;
                }
            }
        }
        for (int i = 1; i <= account.getPower(); i++) {
            if (y - i >= 0) {
                if (POSITIONS[x][y - i][0] == ROCK) {
                    break;
                }

                boolean isBush = false;

                if (POSITIONS[x][y - i][0] == BUSH) {
                    isBush = true;
                    Bush bush = (Bush) BUSH_POS[x][y - i][0];
                    bush.die();
                }

                Vector3f temp = position.clone();
                temp.z = temp.z - (PLACE_HOLDER * 2) * i;
                explode(temp);
                if (isBush) {
                    break;
                }
            }

        }
    }

    private void explode(Vector3f position) {
        Explosion ex = new Explosion(position, assetManager, rootNode);
        ex.setNode(rootNode);
        explodes.add(ex);
        rootNode.attachChild(ex);
        bullet.getPhysicsSpace().add(ex);
        ex.enExplode();
    }
    //</editor-fold>

    private void setUpSound() {
        explodeSound = new AudioNode(assetManager, "Sounds/explode.wav", false);
        explodeSound.setLooping(false);
        explodeSound.setPositional(false);
        explodeSound.setVolume(1f);

        rubySound = new AudioNode(assetManager, "Sounds/ruby.wav", false);
        rubySound.setLooping(false);
        rubySound.setPositional(false);
        rubySound.setVolume(1f);

        wormDieSound = new AudioNode(assetManager, "Sounds/worm die.wav", false);
        wormDieSound.setLooping(false);
        wormDieSound.setVolume(.6f);

        saDieSound = new AudioNode(assetManager, "Sounds/worm laught.wav", false);
        saDieSound.setLooping(false);
        wormDieSound.setVolume(1f);
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public SimpleApplication getApp() {
        return app;
    }

    private void checkExplode() {
        if (!explodes.isEmpty()) {
            for (int i = 0; i < explodes.size(); i++) {
                if (explodes.get(i).isExplode()) {
                    bullet.getPhysicsSpace().remove(explodes.get(i).getControl(GhostControl.class));
                    bullet.getPhysicsSpace().remove(explodes.get(i));
                    explodes.get(i).getDirt().killAllParticles();
                    explodes.get(i).getDirt().removeFromParent();
                    explodes.get(i).detachAllChildren();
                    explodes.get(i).removeFromParent();
                    explodes.remove(explodes.get(i));
                    System.out.println("sa " + sa.isIsShield());
                    account.setShield(sa.isIsShield());
                    System.out.println("account " + account.isShield());
                }
            }
        }
    }

    @Override
    public void update(float tpf) {
        checkExplode();
        if (!worms.isEmpty()) {
            for (int i = 0; i < worms.size(); i++) {
                worms.get(i).moveTo(sa.getControl(CharacterControl.class).getPhysicsLocation());
            }
        } else {
            Random win = new Random();
            if (bushes.isEmpty()) {
                //count free space 
                int counter = 0;
                ArrayList<byte[]> pos = new ArrayList<byte[]>();
                for (int i = 0; i < POSITIONS.length; i++) {
                    for (int j = 0; j < POSITIONS[i].length; j++) {
                        if (POSITIONS[i][j] != null && POSITIONS[i][j][0] == EMPTY) {
                            counter++;
                            byte[] temp = new byte[]{(byte) i, (byte) j};
                            pos.add(temp);
                        }

                    }
                }
                if (!isWon) {
                    int ran = win.nextInt(pos.size());

                    int x = pos.get(ran)[0];
                    int y = pos.get(ran)[1];

                    Win gateWin = new Win(gate);
                    gate.setMaterial(gateMat);

                    POSITIONS[x][y][0] = GATE;
                    rootNode.attachChild(gateWin);
                    bullet.getPhysicsSpace().add(gateWin);
                    gateWin.render(x, y);
                    isWon = true;
                }
            }
        }

        wormAndSa();
        rubyAndSa();
        gateAndSa();
    }

    private void gateAndSa() {
        Vector3f saPos = (Vector3f) SA_POS[0][0];
        byte x = (byte) Math.round((-saPos.x / PLACE_HOLDER + MAP_WIDTH - 1) / 2);
        byte y = (byte) Math.round((saPos.z / PLACE_HOLDER + MAP_HEIGHT - 1) / 2);
        if (POSITIONS[x][y][0] == GATE) {
            saGoHome(saPos);

            bullet.getPhysicsSpace().remove(sa);
            sa.removeFromParent();
            app.end();
        }
    }

    private void saGoHome(Vector3f position) {
        sa.setIsWon(true);

        ParticleEmitter dirt = new ParticleEmitter("Debris", ParticleMesh.Type.Triangle, 200);
        Material debris_mat = new Material(assetManager,
                "Common/MatDefs/Misc/Particle.j3md");
        debris_mat.setTexture("Texture", assetManager.loadTexture(
                "Effects/Explosion/shockwave.png"));
        dirt.setMaterial(debris_mat);
        rootNode.attachChild(dirt);
        dirt.setParticlesPerSec(0);
        dirt.setImagesX(1);
        dirt.setImagesY(1); // 3x3 texture animation
        dirt.setStartSize(1f);
        dirt.setEndSize(5f);
        dirt.setLowLife(0.4f);
        dirt.setHighLife(0.5f);
        dirt.setLocalTranslation(position);
        dirt.getParticleInfluencer().setInitialVelocity(new Vector3f(10f, 5f, 0));
        dirt.getParticleInfluencer().setVelocityVariation(1f);
        dirt.setGravity(0, -5f, 0);

        dirt.emitAllParticles();
    }

    private void rubyAndSa() {
        Vector3f saPos = (Vector3f) SA_POS[0][0];
        byte x = (byte) Math.round((-saPos.x / PLACE_HOLDER + MAP_WIDTH - 1) / 2);
        byte y = (byte) Math.round((saPos.z / PLACE_HOLDER + MAP_HEIGHT - 1) / 2);
        if (POSITIONS[x][y][0] == RUBY) {
            Ruby rub = (Ruby) RUBY_POS[x][y][0];
            POSITIONS[x][y][0] = EMPTY;
            bullet.getPhysicsSpace().remove(rub.getControl(CharacterControl.class));
            bullet.getPhysicsSpace().removeAll(rub);
            rub.detachAllChildren();
            rub.removeFromParent();
            rubies.remove(rub);
            if (!isMute[0]) {
                rubySound.playInstance();
            }
            account.setMoney(account.getMoney() + 1);
            account.addScore(1);
            app.updateMoneyIngame();
        }
    }

    private void wormAndSa() {
        for (int i = 0; i < WORM_POS.length; i++) {
            if (WORM_POS[i] != null) {
                if (WORM_POS[i][1] != null) {
                    Worm worm = (Worm) WORM_POS[i][1];
                    if (!worm.isDead()) {
                        Vector3f saPos = (Vector3f) SA_POS[0][0];
                        Vector3f wormPos = (Vector3f) WORM_POS[i][0];
                        float x = Math.abs(saPos.x - wormPos.x);
                        float z = Math.abs(saPos.z - wormPos.z);
                        if (x <= (PLACE_HOLDER / 2) && z <= (PLACE_HOLDER / 2)) {
                            if (!sa.isDead()) {
                                sa.die();
                            }

                        }

                        byte xW = (byte) Math.round((-wormPos.x / PLACE_HOLDER + MAP_WIDTH - 1) / 2);
                        byte yW = (byte) Math.round((wormPos.z / PLACE_HOLDER + MAP_HEIGHT - 1) / 2);
                        if (POSITIONS[xW][yW][0] == RUBY) {
                            Ruby rub = (Ruby) RUBY_POS[xW][yW][0];
                            POSITIONS[xW][yW][0] = EMPTY;
                            bullet.getPhysicsSpace().remove(rub.getControl(CharacterControl.class));
                            bullet.getPhysicsSpace().removeAll(rub);
                            rub.detachAllChildren();
                            rub.removeFromParent();
                            rubies.remove(rub);
                        }

                        if (POSITIONS[xW][yW][0] == FRUIT && isBoss) {
                            Fruit fruit = (Fruit) BOMB_POS[xW][yW][0];
                            fruits.remove(fruit);
                            POSITIONS[xW][yW][0] = EMPTY;
                            fruit.detachAllChildren();
                            bullet.getPhysicsSpace().remove(fruit);
                            fruit.removeFromParent();
                        }
                    }
                }
            }
        }
    }

    public void pause(boolean a) {
        sa.setIsPause(a);
        for (int i = 0; i < worms.size(); i++) {
            worms.get(i).setIsPause(a);
        }
    }

    @Override
    public void cleanup() {
        bullet.getPhysicsSpace().removeAll(sa);
        sa.removeFromParent();

        bullet.getPhysicsSpace().remove(floor_geo);
        floor_geo.removeFromParent();

        for (int i = 0; i < bushes.size(); i++) {
            bullet.getPhysicsSpace().removeAll(bushes.get(i));
            bushes.get(i).removeFromParent();
        }

        for (int i = 0; i < worms.size(); i++) {
            bullet.getPhysicsSpace().removeAll(worms.get(i));
            worms.get(i).removeFromParent();
        }

        for (int i = 0; i < rubies.size(); i++) {
            bullet.getPhysicsSpace().removeAll(rubies.get(i));
            rubies.get(i).removeFromParent();
        }
    }
}
