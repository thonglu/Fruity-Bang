package GameModel;

import Game.GameConstant;
import GameController.SaGhost;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.GhostControl;
import com.jme3.collision.Collidable;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.TouchListener;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Thong
 */
public class Sa extends CuteModel implements ActionListener, TouchListener, GameConstant, Collidable, AnimEventListener {

    //temp direction
    boolean left = false, right = false, up = false, down = false;
    //temp vectors
    Vector3f walkDirection = new Vector3f();
    //character animation
    private AnimChannel animationChannel;
    //character control in physic world
    private CharacterControl saChar;
    //character default speed
    private float speed;
    private AudioNode footStep;
    private boolean isWon = false;
    private boolean isDead = false;
    private boolean isShield = false;

    public Sa(Spatial s) {
        super(s);
        setName("Sa");
        CapsuleCollisionShape capsule = new CapsuleCollisionShape(PLACE_HOLDER, PLACE_HOLDER * .8f);
        saChar = new SaGhost(capsule, this);

        AnimControl animationControl = s.getControl(AnimControl.class);
        animationControl.addListener(this);

        GhostControl ghost = new GhostControl(capsule);

        animationChannel = animationControl.createChannel();
        addControl(saChar);
        addControl(ghost);
        speed = (float) (BASIC_V * 0.6);
    }

    public void reset(){
        isWon = false;
        isDead = false;
        isPause = false;
        animationChannel.setAnim("Stand");
    }

    public boolean isIsShield() {
        return isShield;
    }

    public void setIsShield(boolean isShield) {
        this.isShield = isShield;
    }
    
    public void speedBoosted() {
        speed = (float) (BASIC_V * 1.5);
    }

    public void render(int x, int y) {
        footStep = new AudioNode(scene.getAssetManager(), "Sounds/sa foot step.wav", false);
        footStep.setLooping(true);
        footStep.setPositional(false);
        footStep.setVolume(.2f);
        scene.getRootNode().attachChild(footStep);
        s.rotate(0, -(float) (Math.PI / 2), 0);
        render(x, y, saChar, .8f);
    }

    @Override
    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("CharLeft")) {
            left = value;
        } else if (binding.equals("CharRight")) {
            right = value;
        } else if (binding.equals("CharUp")) {
            up = value;
        } else if (binding.equals("CharDown")) {
            down = value;
        }

        doAction();

    }

    private void doAction() {
//        walkDirection.set(0, 0, 0);
        if (isDead || isPause) {
            walkDirection.set(0, 0, 0);
            saChar.setWalkDirection(walkDirection);
            footStep.stop();
            return;
        }
        if (left) {
            walkDirection.addLocal(speed, 0, 0);
//            Logger.getLogger("Fruity").log(Level.WARNING, "Sa walk left");
        }
        if (right) {
            walkDirection.addLocal(-speed, 0, 0);
//            Logger.getLogger("Fruity").log(Level.WARNING, "Sa walk right");
        }
        if (up) {
            walkDirection.addLocal(0, 0, speed);
//            Logger.getLogger("Fruity").log(Level.WARNING, "Sa walk up");
        }
        if (down) {
            walkDirection.addLocal(0, 0, -speed);
//            Logger.getLogger("Fruity").log(Level.WARNING, "Sa walk down");
        }

        if (walkDirection.length() == 0) {
            animationChannel.setTime(0);
            if (!"Stand".equals(animationChannel.getAnimationName())) {
//                Logger.getLogger("Fruity").log(Level.WARNING, "Sa stand");
                animationChannel.setAnim("Stand", 0f);
                footStep.stop();
            }
        } else {
            saChar.setViewDirection(walkDirection);
            if (!"Walk".equals(animationChannel.getAnimationName())) {
                animationChannel.setAnim("Walk", 0.0f);
                if(!isMute[0]){
                footStep.play();
            }
            }
        }
        
            saChar.setWalkDirection(walkDirection);
            SA_POS[0][0] = saChar.getPhysicsLocation();
//        isDead = false;
       
        walkDirection.set(0, 0, 0);
    }

    @Override
    public void onTouch(String name, TouchEvent e, float tpf) {
        Logger.getLogger("Fruity").log(Level.WARNING, "TouchEvent received");
        int width = scene.getApp().getContext().getSettings().getWidth();
        int height = scene.getApp().getContext().getSettings().getHeight();
        float x = e.getX();
        float y = e.getY();
        String identifier = "";

        if (x > width / 3 && x < width * 2 / 3
                && y > height / 3 && y <= height * 2 / 3) {
            scene.plantBomb();
        }
        if (y >= height / 3 && y <= (height * 2 / 3)) {// left or right
            if (x < width / 3) {// left
                identifier = "CharLeft";
            } else if (x > width * 2 / 3) {//right
                identifier = "CharRight";
            }
        } else if (y > height / 3) {//Up
            identifier = "CharUp";
        } else {//Down
            identifier = "CharDown";
        }

        switch (e.getType()) {
            case DOWN:
                Logger.getLogger("Fruity").log(Level.WARNING, "TouchEvent is DOWN");
                Logger.getLogger("Fruity").log(Level.WARNING, "identifier: {0}", identifier);

                onAction(identifier, true, tpf);
                break;
            case UP:
                Logger.getLogger("Fruity").log(Level.WARNING, "TouchEvent is UP");
                Logger.getLogger("Fruity").log(Level.WARNING, "identifier: {0}", identifier);

                walkDirection.set(0f, 0f, 0f);
                saChar.setWalkDirection(walkDirection);
                onAction(identifier, false, tpf);
                break;
        }

        e.setConsumed();
    }

    public void die() {
        speed = (float) (BASIC_V * 0.6);
        animationChannel.setAnim("Die");
        isDead = true;
    }

    public boolean isDead() {
        return isDead;
    }

    public boolean isWon() {
        return isWon;
    }

    public void setIsWon(boolean isWon) {
        this.isWon = isWon;
    }

    @Override
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
        if (animName.equals("Die")) {
            scene.saDie(this);
        }
    }

    @Override
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
    }
}
