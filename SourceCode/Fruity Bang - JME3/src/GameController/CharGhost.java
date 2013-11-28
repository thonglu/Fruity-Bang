/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GameController;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.scene.Node;

/**
 *
 * @author Thong
 */
public class CharGhost extends CharacterControl{
    
    protected Node T;

    
    public CharGhost(CollisionShape shape,Node T) {
        super(shape,0f);
        this.T = T;
    }
    
    
    
}
