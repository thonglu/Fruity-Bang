/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GameController;

import GameModel.Worm;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.scene.Node;

/**
 *
 * @author Thong
 */
public class WormGhost extends CharGhost{

    public WormGhost(CollisionShape shape, Node T) {
        super(shape, T);
    }
    
    public Worm getWorm(){
        return ((Worm)T);
    }
    
}
