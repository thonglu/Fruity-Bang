package GameModel;

import Game.GameConstant;
import Game.Scene;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * @author Thong
 */
public class CuteModel extends Node implements GameConstant {

    protected static final float BASIC_V = 1f;
    protected Spatial s;
    protected Scene scene;
    protected boolean isPause = false;

    public CuteModel(Spatial s) {
        this.s = s;
    }

    public void setSkin(Material m) {
        s.setMaterial(m);
    }

    public void setScene(Scene s) {
        this.scene = s;
    }

    public void setIsPause(boolean isPause) {
        this.isPause = isPause;
    }

    
    protected void render(int x, int y, CharacterControl control, float ratio) {
        attachChild(s);
        s.center();
        setLocalScale(PLACE_HOLDER * ratio, PLACE_HOLDER * ratio, PLACE_HOLDER * ratio);

        control.setPhysicsLocation(new Vector3f(PLACE_HOLDER * (MAP_WIDTH - 1 - 2 * x),
                PLACE_HOLDER, PLACE_HOLDER * -(MAP_HEIGHT - 1 - 2 * y)));
    }
    

    protected void render(int x, int y, RigidBodyControl control, float ratio) {
        attachChild(s);
        s.center();

        setLocalScale(PLACE_HOLDER * ratio, PLACE_HOLDER * ratio, PLACE_HOLDER * ratio);

        control.setPhysicsLocation(new Vector3f(PLACE_HOLDER * (MAP_WIDTH - 1 - 2 * x),
                PLACE_HOLDER, PLACE_HOLDER * -(MAP_HEIGHT - 1 - 2 * y)));
    }
    
    protected void render(int x, int y, RigidBodyControl control, float ratio, float height) {
        attachChild(s);
        s.center();

        setLocalScale(PLACE_HOLDER * ratio, PLACE_HOLDER * ratio, PLACE_HOLDER * ratio);

        control.setPhysicsLocation(new Vector3f(PLACE_HOLDER * (MAP_WIDTH - 1 - 2 * x),
                height, PLACE_HOLDER * -(MAP_HEIGHT - 1 - 2 * y)));
    }
}
