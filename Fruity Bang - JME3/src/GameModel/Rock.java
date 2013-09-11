package GameModel;

import Game.GameConstant;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 * @author Thong
 */
public class Rock extends CuteModel implements GameConstant {

    private RigidBodyControl rockControl;

    public Rock(Spatial s) {
        super(s);
        setName("Rock");
        BoxCollisionShape capsule = new BoxCollisionShape(new Vector3f(PLACE_HOLDER, PLACE_HOLDER ,PLACE_HOLDER));
        rockControl = new RigidBodyControl(capsule, 0f);
        addControl(rockControl);
    }

    /**
     *
     * @param x the x position of the rock on the map (2D)
     * @param y the y position of the rock on the map (2D)
     */
    public void render(int x, int y) {
        float ratio = 1f;
        render(x, y, rockControl, ratio);
    }
}
