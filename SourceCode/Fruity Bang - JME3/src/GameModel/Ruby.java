/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GameModel;

import Game.GameConstant;
import com.jme3.animation.AnimChannel;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 *
 * @author Thong
 */
public class Ruby extends CuteModel implements GameConstant{

    private CharacterControl rubyControl;
    
    public Ruby(Spatial s) {
        super(s);
        setName("Ruby");
        
         BoxCollisionShape capsule = new BoxCollisionShape(new Vector3f(PLACE_HOLDER, PLACE_HOLDER ,PLACE_HOLDER));
         
         rubyControl = new CharacterControl(capsule,0f);
         
         addControl(rubyControl);
    }
    
     public void render(int x, int y) {
        float ratio = .4f;
        render(x, y, rubyControl, ratio );
    }
    
    
}
