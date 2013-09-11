/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GameController;

import GameModel.Sa;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.scene.Node;

/**
 *
 * @author Thong
 */
public class SaGhost extends CharGhost{

    public SaGhost(CollisionShape shape,Node sa) {
        super(shape,sa);
    }
    
    public Sa getSa(){
        return (Sa)T;
    }
    
}
