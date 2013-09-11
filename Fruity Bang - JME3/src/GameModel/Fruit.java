package GameModel;

import Game.GameConstant;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.collision.Collidable;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Thong
 */
public class Fruit extends CuteModel implements GameConstant, AnimEventListener, Collidable {

    private CharacterControl fruitControl;
    private AnimChannel animationChannel;
    private boolean isFruit = false;
    private TimerTask life;

    public Fruit(Spatial s) {
        super(s);
        setName("Fruit");
        BoxCollisionShape capsule = new BoxCollisionShape(new Vector3f(PLACE_HOLDER, PLACE_HOLDER ,PLACE_HOLDER));
        fruitControl = new CharacterControl(capsule, 0f);
        AnimControl animationControl = s.getControl(AnimControl.class);
        animationControl.addListener(this);

        animationChannel = animationControl.createChannel();
        animationChannel.setAnim("Bounce");

        addControl(fruitControl);
        setBang();
    }

//    private void saAndFruit() {
//        List<PhysicsRayTestResult> test = bullet.rayTest(Vector3f.ZERO, fruitControl.getPhysicsLocation());
//        if (test.size() > 0) {
//
//            PhysicsRayTestResult getObject = test.get(0);
//            PhysicsCollisionObject collisionObject = getObject.getCollisionObject();
//
//            if (collisionObject.getUserObject() instanceof Sa) {
//                
//            } else {
//                Vector3f position = fruitControl.getPhysicsLocation();
//                CapsuleCollisionShape capsule = new CapsuleCollisionShape(PLACE_HOLDER, PLACE_HOLDER * 0.2f);
//                RigidBodyControl temp = new RigidBodyControl(capsule, 0f);
//                temp.setPhysicsLocation(position);
//                addControl(temp);
//                bullet.add(temp);
//            }
//
//
//        }

//        CollisionResults results = new CollisionResults();
//        BoundingVolume b = getWorldBound();
//        sa.getSpatial().collideWith(b, results);
//
//        if (results.size() > 0) {
//            isFruit = true;
//        } else {
//            if (isFruit && getControl(RigidBodyControl.class) == null) {
//                System.out.println("sa");
//                Vector3f position = fruitControl.getPhysicsLocation();
//                CapsuleCollisionShape capsule = new CapsuleCollisionShape(PLACE_HOLDER, PLACE_HOLDER * 0.2f);
//                RigidBodyControl temp = new RigidBodyControl(capsule, 0f);
//                temp.setPhysicsLocation(position);
//                addControl(temp);
//                bullet.add(temp);
//            }
//        }
//    }

    public void render(int x, int y, float ratio) {
//        float ratio = .8f;
        render(x, y, fruitControl, ratio );
    }

    private void setBang() {
//        life = new TimerTask() {
//            @Override
//            public void run() {
//                saAndFruit();
//            }
//        };
        Timer timer = new Timer();
//        timer.scheduleAtFixedRate(life, 0, 100);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                animationChannel.setAnim("Explode");
//                life.cancel();
                cancel();
            }
        }, 3000, 3000);
    }

    @Override
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
        if (animName.equals("Explode")) {
            scene.bang(this);
        }
    }

    @Override
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
    }
}
