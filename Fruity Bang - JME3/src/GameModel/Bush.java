package GameModel;

import Game.GameConstant;
import Game.Scene;
import GameController.BushGhost;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 * @author Thong
 */
public class Bush extends CuteModel implements GameConstant, AnimEventListener {

    private RigidBodyControl bushControl;
    private AnimChannel animationChannel;
    
    public Bush(Spatial s) {
        super(s);

        setName("Bush");
        BoxCollisionShape shape = new BoxCollisionShape(new Vector3f(PLACE_HOLDER, PLACE_HOLDER,PLACE_HOLDER));
        bushControl = new BushGhost(shape, this);
        
        addControl(bushControl);
        
        AnimControl animationControl = s.getControl(AnimControl.class);
        animationControl.addListener(this);
       
        animationChannel = animationControl.createChannel();
        animationChannel.setAnim("Stand");
    }

    public void render(int x, int y) {
        float ratio = 1f;
        render(x, y, bushControl, ratio);
    }
    
    public void die() {
        animationChannel.setAnim("Die");
    }
    
    @Override
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
        if(animName.equals("Die")){
            scene.bushDie(this);
        }
    }

    @Override
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
       
    }
}
