package Game;

import Model.Account;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.ChaseCamera;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
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
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Thong
 */
public class Home extends AbstractAppState implements ScreenController, GameConstant {

    private Game app;
    private Node pivot = new Node("root");
    private AssetManager assetManager;
    private Camera cam;
    private Account account;
    private BulletAppState bullet;
    private Spatial rockSpatial;
    private Spatial sa;
    private Nifty nifty;
    private Store store;
    private final float cameraSpeed = 0.8f;
    private AudioNode welcomeSound;

    public Home(AppStateManager stateManager, Game app) {
        this.app = app;
        this.assetManager = this.app.getAssetManager();
        this.cam = this.app.getCamera();
        account = app.getAccount();
        store = app.getStore();

        pivot.attachChild(assetManager.loadModel("Models/skyHome.j3o"));
        bullet = new BulletAppState();

        stateManager.attach(bullet);

        setUpMainChar();
        setUpMap();

        setUpLight();
        setUpRock();
        setUpBushWorm();
        setUpItem();
        setUpSound();
    }

    public Node getRootNode() {
        return pivot;
    }

    private void setUpSound() {
        welcomeSound = new AudioNode(assetManager, "Sounds/welcomeSound.wav", false);
        welcomeSound.setLooping(true);
        welcomeSound.setPositional(false);
        welcomeSound.setVolume(.3f);
    }

    //<editor-fold defaultstate="collapsed" desc="setUpBushWorm">
    private void setUpBushWorm() {
        Spatial worms = assetManager.loadModel("Models/worm1/worm.j3o");
        Spatial bushes = assetManager.loadModel("Models/bush/bush.j3o");

        worms.rotate(0f, -(float) Math.PI / 4, 0f);
        Material wormMaterial = assetManager.loadMaterial("Materials/worm1.j3m");
        Material bushMaterial = assetManager.loadMaterial("Materials/bush.j3m");

        worms.setLocalScale(.9f, .9f, .9f);

        RigidBodyControl worm = new RigidBodyControl(0f);
        RigidBodyControl bush = new RigidBodyControl(1f);
        worms.addControl(worm);
        bushes.addControl(bush);

        worm.setPhysicsLocation(new Vector3f(1f, .8f, -5f));
        bush.setPhysicsLocation(new Vector3f(0f, 0f, -2f));

        worms.setMaterial(wormMaterial);
        bushes.setMaterial(bushMaterial);

        pivot.attachChild(bushes);
        bullet.getPhysicsSpace().add(bushes);

        pivot.attachChild(worms);
        bullet.getPhysicsSpace().add(worms);

    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="setUpRock">
    private void setUpRock() {
        rockSpatial = assetManager.loadModel("Models/rock/rock.j3o");
        Material rockMaterial = assetManager.loadMaterial("Materials/rock.j3m");
        rockSpatial.setMaterial(rockMaterial);

        RigidBodyControl rock = new RigidBodyControl(1f);

        rockSpatial.addControl(rock);
        rock.setPhysicsLocation(new Vector3f(0f, 0f, 2f));

        pivot.attachChild(rockSpatial);

        bullet.getPhysicsSpace().add(rock);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="setUpLight">
    private void setUpLight() {
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(.7f));
        pivot.addLight(al);

        DirectionalLight sun = new DirectionalLight();
        sun.setColor(ColorRGBA.White.mult(.8f));
//        sun.setDirection(new Vector3f(0f, -.8f, 1f).normalizeLocal());
        sun.setDirection(cam.getDirection());
        pivot.addLight(sun);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="setUpCamera">
    public void setUpCammera() {
//        ChaseCamera came = new ChaseCamera(cam, rockSpatial);
        cam.setLocation(new Vector3f(17f, 7f, 0f));
        cam.setRotation(new Quaternion(new float[]{(float) Math.PI / 10, -(float) Math.PI / 2, 0}));

    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="setUpMap">
    private void setUpMap() {
        Material floor_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey key3 = new TextureKey("Textures/ground.png");

        key3.setGenerateMips(true);

        Texture tex3 = assetManager.loadTexture(key3);
        tex3.setWrap(WrapMode.Repeat);
        floor_mat.setTexture("ColorMap", tex3);

        Box floor = new Box(20f, 0.0f, 20f);
        floor.scaleTextureCoordinates(new Vector2f(20, 20));

        Geometry floor_geo = new Geometry("Floor", floor);
        floor_geo.setMaterial(floor_mat);
        pivot.attachChild(floor_geo);
        /* Make the floor physical with mass 0.0f! */
        RigidBodyControl floor_phy = new RigidBodyControl(0.0f);
        floor_geo.addControl(floor_phy);
        floor_phy.setPhysicsLocation(new Vector3f(0, 0, 0));
        bullet.getPhysicsSpace().add(floor_geo);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Main Character Sa">
    private void setUpMainChar() {
        //create the main character
        sa = assetManager.loadModel("Models/sa/sa.j3o");
        sa.setMaterial(assetManager.loadMaterial("Materials/sa.j3m"));

        RigidBodyControl saChar = new RigidBodyControl(0f);
        sa.rotate(0f, (float) Math.PI / 4, 0f);
        sa.addControl(saChar);
        saChar.setPhysicsLocation(new Vector3f(0f, 0f, 6f));
        sa.rotate(0f, -(float) Math.PI / 4, 0f);

        pivot.attachChild(sa);
        bullet.getPhysicsSpace().add(sa);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="setUpItem()">
    private void setUpItem() {
        Spatial shield = assetManager.loadModel("Models/shield/shield.j3o");
        Spatial power = assetManager.loadModel("Models/power/power.j3o");
        Spatial multiBomb = assetManager.loadModel("Models/multiplebomb/multibomb.j3o");

        Material shieldMat = assetManager.loadMaterial("Materials/shield.j3m");
        Material powerMat = assetManager.loadMaterial("Materials/power.j3m");
        Material multibombMat = assetManager.loadMaterial("Materials/multibomb.j3m");

        shield.setMaterial(shieldMat);
        power.setMaterial(powerMat);
        multiBomb.setMaterial(multibombMat);

        pivot.attachChild(shield);
        pivot.attachChild(power);
        pivot.attachChild(multiBomb);

        shield.setLocalTranslation(5f, 56.3f, .1f);
        shield.scale(1.5f);
        AnimChannel shieldAnim = shield.getControl(AnimControl.class).createChannel();
        shieldAnim.setAnim("Bounce");

        power.setLocalTranslation(5f, 56.3f, 4.8f);
        power.scale(1.5f);
        power.rotate(0f, (float) Math.PI / 2, .0f);
        AnimChannel powerAnim = power.getControl(AnimControl.class).createChannel();
        powerAnim.setAnim("Bounce");

        multiBomb.setLocalTranslation(5f, 56.3f, -5.4f);
        multiBomb.rotate(0f, (float) Math.PI / 2, 0f);
        multiBomb.scale(.7f);
        AnimChannel multiAnim = multiBomb.getControl(AnimControl.class).createChannel();
        multiAnim.setAnim("Bounce");
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="chooseLevel()">
    public void chooseLevel() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Vector3f temp = cam.getLocation();
                temp.y = temp.y + cameraSpeed;
                cam.setLocation(temp);
                if (temp.y >= 30f && temp.y <= 32f) {
                    cancel();
                    nifty.gotoScreen("lv");
                }
            }
        }, 0, 5);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="store()">
    public void store() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Vector3f temp = cam.getLocation();
                temp.y = temp.y + (cameraSpeed - .4f);
                cam.setLocation(temp);
                if (temp.y >= 60f && temp.y <= 62f) {
                    cancel();

                    nifty.gotoScreen("store");
                    updateMoney();

                    updateStore();
                }
            }
        }, 0, 5);
    }
    //</editor-fold>

    private void updateStore() {
        Screen screen = nifty.getCurrentScreen();
        store.init();

        Element element = screen.findElementByName("power_price");
        element.getRenderer(TextRenderer.class).setText(store.getPowerPrice());

        element = screen.findElementByName("shield_price");
        element.getRenderer(TextRenderer.class).setText(store.getShieldPrice());

        element = screen.findElementByName("multi_price");
        element.getRenderer(TextRenderer.class).setText(store.getBombPrice());

        element = screen.findElementByName("money");
        element.getRenderer(TextRenderer.class).setText(account.getMoney() + "");

        element = screen.findElementByName("shield");
        element.getRenderer(TextRenderer.class).setText("Dangered");

        element = screen.findElementByName("power");
        element.getRenderer(TextRenderer.class).setText(account.getPower() + "/4 - " + account.getFruitString());

        element = screen.findElementByName("multi");
        element.getRenderer(TextRenderer.class).setText(account.getCapacity() + "/5");
    }

    public void startLevel(String lv) {
        byte lvl = Byte.parseByte(lv);
        app.startPlaying(lvl);
    }

    //<editor-fold defaultstate="collasped" desc="mute()">
    public void mute() {
        if (isMute[0]) {
            welcomeSound.play();
            isMute[0] = false;
        } else {
            welcomeSound.stop();
            isMute[0] = true;;
        }
        app.setMute();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="back()">
    public void back() {
        if (nifty.getCurrentScreen().getScreenId().equals("lv")) {
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Vector3f temp = cam.getLocation();
                    temp.y = temp.y - cameraSpeed;
                    cam.setLocation(temp);
                    if (temp.y >= 5.5f && temp.y <= 8.5f) {
                        temp.y = 7f;
                        cam.setLocation(temp);
                        cancel();
                        nifty.gotoScreen("start");
                    }
                }
            }, 0, 5);

        } else if (nifty.getCurrentScreen().getScreenId().equals("store")) {
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Vector3f temp = cam.getLocation();
                    temp.y = temp.y - (cameraSpeed - .4f);
                    cam.setLocation(temp);
                    if (temp.y >= 49f && temp.y <= 51f) {
                        cam.setLocation(temp);
                        cancel();
                        nifty.gotoScreen("lv");
                    }
                }
            }, 0, 5);
        }
    }
    //</editor-fold>

    public void buyMulti() {
        if (store.buyBomb()) {
            Element bombElement = nifty.getCurrentScreen().findElementByName("multi_price");
            bombElement.getRenderer(TextRenderer.class).setText(store.getBombPrice());

            bombElement = nifty.getCurrentScreen().findElementByName("multi");
            bombElement.getRenderer(TextRenderer.class).setText(account.getCapacity() + "/5");

            updateMoney();
        }
    }

    public void buyPower() {
        if (store.buyPower()) {
            Element powerElement = nifty.getCurrentScreen().findElementByName("power_price");
            powerElement.getRenderer(TextRenderer.class).setText(store.getPowerPrice());

            powerElement = nifty.getCurrentScreen().findElementByName("power");
            powerElement.getRenderer(TextRenderer.class).setText(account.getPower() + "/4 - " + account.getFruitString());
            updateMoney();
        }
    }

    public void buyShield() {
        if (store.buyShield()) {
            Element shieldElement = nifty.getCurrentScreen().findElementByName("shield_price");
            shieldElement.getRenderer(TextRenderer.class).setText(store.getShieldPrice());

            shieldElement = nifty.getCurrentScreen().findElementByName("shield");
            if (account.isShield()) {
                shieldElement.getRenderer(TextRenderer.class).setText("Secured");
            } else {
                shieldElement.getRenderer(TextRenderer.class).setText("Dangered");
            }
            updateMoney();
        }
    }

    private void updateMoney() {
//        Element moneyElement = nifty.getCurrentScreen().findElementByName("money");
//        moneyElement.getRenderer(TextRenderer.class).setText(account.getMoney() + " Ruby");
        app.updateMoney();
    }

    @Override
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
    }

    public AudioNode getWelcomeSound() {
        return welcomeSound;
    }

    @Override
    public void onStartScreen() {
        System.out.println("start");
        try {
            if (nifty.getCurrentScreen().getScreenId().equals("start") && !isMute[0]) {
                welcomeSound.play();
            } else {
            }
        } catch (Exception e) {
            System.out.println("Play sound again");
        }
    }

    public void cheat() {
        System.out.println("Cheat");
        account.addScore(100);
        account.setMoney(account.getMoney() + 100);
    }

    @Override
    public void onEndScreen() {
    }
}
