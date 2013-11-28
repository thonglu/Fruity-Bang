/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GameModel;

import Game.GameConstant;
import GameController.BushGhost;
import GameController.SaGhost;
import GameController.WormGhost;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.GhostControl;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Cole
 */
public class Explosion extends Node implements GameConstant {

//    private CharacterControl effectControl;
    private Vector3f position;
    private AssetManager assetManager;
    private Node rootNode;
    private boolean isExplode = false;
    private ParticleEmitter dirt;
    
    private AudioNode shieldBroke;
    
    private GhostControl ghost;
    
    private Node explode;
    
    public Explosion(Vector3f position, AssetManager a, Node rootNode) {
        setName("Explode");
        this.position = position;
        this.assetManager = a;
        this.rootNode = rootNode;

        BoxCollisionShape box = new BoxCollisionShape(new Vector3f(PLACE_HOLDER-1, PLACE_HOLDER, PLACE_HOLDER-1));
        
        ghost = new GhostControl(box);
        addControl(ghost);
        
        ghost.setPhysicsLocation(position);

        explode();
        
        shieldBroke = new AudioNode(assetManager, "Sounds/shield broke.wav",false);
        shieldBroke.setLooping(false);
        shieldBroke.setPositional(false);
        shieldBroke.setVolume(.9f);

    }
    
    public void setNode(Node node){
        this.explode = node;
    }
    
    public boolean isExplode() {

        List<PhysicsCollisionObject> collision = ghost.getOverlappingObjects();
                for (int i = 0; i < collision.size(); i++) {
                    if(collision.get(i) instanceof SaGhost){
                        SaGhost sa = (SaGhost) collision.get(i);
                        if(sa.getSa().isIsShield()){
                            sa.getSa().setIsShield(false);
                            
                            shieldBroke.playInstance();
                            continue;
                        }
                        sa.getSa().die();
                    } else if(collision.get(i) instanceof WormGhost) {
                        WormGhost worm = (WormGhost) collision.get(i);
                        worm.getWorm().die();
                    }
                }
        return isExplode;
    }

    public void enExplode() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                isExplode = true;
                cancel();
            }
        }, 100, 200);
    }

    public ParticleEmitter getDirt(){
        return dirt;
    }
    
    private void explode() {
        dirt = new ParticleEmitter("Debris", ParticleMesh.Type.Triangle, 200);
        Material debris_mat = new Material(assetManager,
                "Common/MatDefs/Misc/Particle.j3md");
        debris_mat.setTexture("Texture", assetManager.loadTexture(
                "Effects/Explosion/smoketrail.png"));
        dirt.setMaterial(debris_mat);
        rootNode.attachChild(dirt);
        dirt.setParticlesPerSec(0);
        dirt.setImagesX(1);
        dirt.setImagesY(3); // 3x3 texture animation
        dirt.setStartSize(1f);
        dirt.setEndSize(5f);
        dirt.setLowLife(0.4f);
        dirt.setHighLife(0.5f);
        dirt.setLocalTranslation(position);
        dirt.getParticleInfluencer().setInitialVelocity(new Vector3f(10f, 5f, 0));
        dirt.getParticleInfluencer().setVelocityVariation(1f);
        dirt.setGravity(0, -5f, 0);
        
        dirt.emitAllParticles();
    }
}
