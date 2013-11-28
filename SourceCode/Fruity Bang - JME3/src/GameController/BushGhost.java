/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GameController;

import GameModel.Bush;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Node;

/**
 *
 * @author Thong
 */
public class BushGhost extends RigidBodyControl{

    private Bush bush;
    
    public BushGhost(CollisionShape shape, Bush bush) {
        super(shape, 0f);
        this.bush = bush;
    }

    
    public Bush getBush() {
        return bush;
    }
    
}
