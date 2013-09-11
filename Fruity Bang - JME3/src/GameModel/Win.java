/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GameModel;

import com.jme3.animation.AnimChannel;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 *
 * @author Thong
 */
public class Win extends CuteModel{
    
    private CharacterControl rubyControl;
    
    public Win(Spatial s) {
        super(s);
        setName("Ruby");
        
         BoxCollisionShape capsule = new BoxCollisionShape(new Vector3f(PLACE_HOLDER, PLACE_HOLDER*0.6f ,PLACE_HOLDER));
         
         rubyControl = new CharacterControl(capsule,0f);
         
         addControl(rubyControl);
    }
    
     public void render(int x, int y) {
        float ratio = .8f;
        render(x, y, rubyControl, ratio );
    }
    
}
